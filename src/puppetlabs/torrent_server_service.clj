(ns puppetlabs.torrent-server-service
  (:require [clojure.tools.logging :as log]
            [puppetlabs.torrent-server-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper])
  (:import (java.net InetSocketAddress)
           (com.turn.ttorrent.tracker Tracker)))

(defprotocol TorrentService
  (torrent [this caller]))

(def tracker (Tracker. (InetSocketAddress. 6969)))

(trapperkeeper/defservice torrent-service
  TorrentService
  []
  (init [this context]
    (log/info "Initializing torrent service")
    context)
  (start [this context]
    (log/info "Starting torrent service")
    (.start tracker)
    context)
  (stop [this context]
    (log/info "Shutting down torrent service")
    (.stop tracker)
    context)
  (torrent [this caller]
           (core/get-torrent tracker caller)))
