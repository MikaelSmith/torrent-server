(ns puppetlabs.test-helpers
  (:import (java.net InetSocketAddress)
           (com.turn.ttorrent.tracker Tracker)))

(defn normalize-torrent [torrent]
  (clojure.string/replace torrent #"creation datei\d+" "creation date"))

(defn slurp-torrent [torrent]
  (normalize-torrent (slurp torrent)))

(defmacro with-tracker
  [& body]
  `(let [~'tracker (Tracker. (InetSocketAddress. 6969))]
     (.start ~'tracker)
     ~@body
     (.stop ~'tracker)
     ))
