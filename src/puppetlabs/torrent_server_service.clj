(ns puppetlabs.torrent-server-service
  (:require [clojure.tools.logging :as log]
            [puppetlabs.torrent-server-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper])
  (:import (java.net InetSocketAddress)
           (com.turn.ttorrent.tracker Tracker)))

(defprotocol HelloService
  (hello [this caller]))

(def tracker (Tracker. (InetSocketAddress. 6969)))

(trapperkeeper/defservice hello-service
  HelloService
  []
  (init [this context]
    (log/info "Initializing hello service")
    context)
  (start [this context]
    (log/info "Starting hello service")
    (.start tracker)
    context)
  (stop [this context]
    (log/info "Shutting down hello service")
    (.stop tracker)
    context)
  (hello [this caller]
         (core/hello tracker caller)))
