(ns puppetlabs.torrent-server-service
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [puppetlabs.torrent-server-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services])
  (:import (java.net InetSocketAddress)
           (com.turn.ttorrent.tracker Tracker)))

(defprotocol TorrentService
  (torrent [this caller]))

(def default-tracker-port 6969)

(trapperkeeper/defservice torrent-service
  TorrentService
  [[:ConfigService get-in-config]]
  (init [this context]
    (log/info "Initializing torrent service")
    (let [port (if-let [config-port (get-in-config [:torrent-server :tracker-port])]
                 (if (integer? config-port) config-port (read-string config-port))
                 default-tracker-port)
          file-source (get-in-config [:torrent-server :file-source])]
      (log/info "Verifying" port "and" file-source)
      (cond
        (not (integer? port)) (throw (Exception. "torrent-server port must be an integer"))
        (nil? file-source) (throw (Exception. "torrent-server requires file-source option be configured"))
        (not (.isDirectory (io/file file-source)))
          (throw (Exception. (str "torrent-server's file-source directory " file-source " does not exist"))))
      (log/info "Creating tracker")
      (assoc context
             :tracker (Tracker. (InetSocketAddress. port))
             :file-source file-source)))
  (start [this context]
    (log/info "Starting torrent service")
    (.start (:tracker context))
    context)
  (stop [this context]
    (log/info "Shutting down torrent service")
    (if-let [tracker (:tracker context)] (.stop tracker))
    context)
  (torrent [this file]
    (let [context (tk-services/service-context this)
          file-source (:file-source context)
          tracker (:tracker context)]
      (core/get-torrent tracker file-source file))))
