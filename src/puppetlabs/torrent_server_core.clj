(ns puppetlabs.torrent-server-core
  (:import
    (java.net InetSocketAddress)
    (com.turn.ttorrent.tracker Tracker)
    ))

(defn hello
  "Say hello to caller"
  [tracker caller]
  (format "Hello, %s!" caller))
