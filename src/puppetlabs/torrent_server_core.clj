(ns puppetlabs.torrent-server-core
  (:import
    (java.net InetSocketAddress)
    (com.turn.ttorrent.tracker Tracker)
    ))

(defn get-torrent
  "Return torrent for file"
  [tracker file]
  ; Look for existing .torrent file
  ; If not found: find file, generate .torrent, add to tracker
  ; Return magnet URL or .torrent contents to caller
  (format "Hello, %s!" file))
