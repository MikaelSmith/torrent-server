(ns puppetlabs.torrent-server-core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import
    (java.net InetAddress)
    (com.turn.ttorrent.client Client SharedTorrent)
    (com.turn.ttorrent.tracker Tracker TrackedTorrent)
    (com.turn.ttorrent.common Torrent)
    ))

(defn seed-torrent
  "Start sharing the specified torrent"
  [torrent file-source]
  (let [shared-torrent (SharedTorrent. torrent (io/file file-source) true)
        client (Client. (InetAddress/getLocalHost) shared-torrent)]
    (.share client)))

(defn create-torrent
  "Generate a torrent object for specified file, announce to tracker and start seeding"
  [file-source file tracker]
  (let [announce-uri (.toURI (.getAnnounceUrl tracker))
        torrent (Torrent/create (io/file file-source file) announce-uri "torrent-server")]
    (.announce tracker (TrackedTorrent. torrent))
    (seed-torrent torrent file-source)
    torrent))

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
    (catch java.io.IOException e nil)))
