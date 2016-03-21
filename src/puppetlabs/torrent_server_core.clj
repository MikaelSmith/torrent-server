(ns puppetlabs.torrent-server-core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import
    (java.net InetSocketAddress InetAddress URI)
    (com.turn.ttorrent.client Client SharedTorrent)
    (com.turn.ttorrent.tracker Tracker TrackedTorrent)
    (com.turn.ttorrent.common Torrent)
    ))

(def file-location "tmp")
(def localhost (InetAddress/getLocalHost))
(def announce-uri (URI. (str "http://" (.getHostName localhost) ":6969/announce")))

(defn seed-torrent
  "Start sharing the specified torrent"
  [torrent]
  (let [shared-torrent (SharedTorrent. torrent (io/file file-location) true)
        client (Client. localhost shared-torrent)]
    (.share client)))

(defn create-torrent
  "Generate a torrent object for specified file, announce to tracker and start seeding"
  [file tracker]
  (let [torrent (Torrent/create (io/file file-location file) announce-uri "torrent-server")]
    (.announce tracker (TrackedTorrent. torrent))
    (seed-torrent torrent)
    torrent))

(defn get-torrent
  "Return torrent for file"
  [tracker file]
  ; Look for existing .torrent file
  ; If not found: find file, generate .torrent, add to tracker
  ; Return magnet URL or .torrent contents to caller
  (let [trackedTorrents (.getTrackedTorrents tracker)
        torrentByName (first (filter #(= file (.getName %)) trackedTorrents))
        torrent (if (nil? torrentByName)
                  (do (log/info "Adding torrent for" file)
                      (create-torrent file tracker))
                  torrentByName)]
    (io/input-stream (.getEncoded torrent))))
