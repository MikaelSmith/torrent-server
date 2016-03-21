(ns puppetlabs.torrent-server-core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import
    (java.net InetSocketAddress URI)
    (com.turn.ttorrent.tracker Tracker TrackedTorrent)
    (com.turn.ttorrent.common Torrent)
    ))

(def file-location "tmp")
(def localhost (URI. "localhost:6969"))

(defn create-torrent
  "Generate a torrent object for specified file"
  [file]
  (Torrent/create (io/file file-location file) localhost "torrent-server"))

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
                      (create-torrent file))
                  torrentByName)]
    (.announce tracker (TrackedTorrent. torrent))
    (io/input-stream (.getEncoded torrent))))
