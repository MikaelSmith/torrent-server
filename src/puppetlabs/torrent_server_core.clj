(ns puppetlabs.torrent-server-core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import
    (java.net InetAddress)
    (java.io IOException)
    (com.turn.ttorrent.client Client SharedTorrent)
    (com.turn.ttorrent.tracker Tracker TrackedTorrent)
    (com.turn.ttorrent.common Torrent)
    ))

(defn seed-torrent
  "Start sharing the specified torrent; seeding continues indefinitely"
  [torrent file-source]
  (let [shared-torrent (SharedTorrent. torrent (io/file file-source) true)
        client (Client. (InetAddress/getLocalHost) shared-torrent)]
    (.share client)))

(defn throw-if-unsafe
  "Throws if the file path is unsafe, i.e. it uses '..' in the path"
  [file-obj]
  (if-not (.equals (.getAbsolutePath file-obj) (.getCanonicalPath file-obj))
    (throw (IOException. "file request attempted relative file lookup"))))

(defn create-torrent
  "Generate a torrent object for specified file, announce to tracker and start seeding"
  [file-source file tracker]
  ; Ensure the absolute path is the canonical path, to guard against relative files
  (let [file-obj (io/file file-source file)]
    (throw-if-unsafe file-obj)
    (let [announce-uri (.toURI (.getAnnounceUrl tracker))
          torrent (Torrent/create file-obj announce-uri "torrent-server")]
      (.announce tracker (TrackedTorrent. torrent))
      (seed-torrent torrent file-source)
      torrent)))

(defn get-torrent
  "Return torrent for file"
  [tracker file-source file]
  ; Look for existing .torrent file
  ; If not found: find file, generate .torrent, add to tracker
  ; Return .torrent contents to caller
  (try
    (let [trackedTorrents (.getTrackedTorrents tracker)
          torrentByName (first (filter #(= file (.getName %)) trackedTorrents))
          torrent (if (nil? torrentByName)
                    (do (log/info "Adding torrent for" file)
                        (create-torrent file-source file tracker))
                    torrentByName)]
      (io/input-stream (.getEncoded torrent)))
    (catch IOException e
      (log/info "Failure serving file" file ":" (.getMessage e))
      nil)))
