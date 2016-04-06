(ns puppetlabs.torrent-server-web-service
  (:require [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [puppetlabs.torrent-server-web-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services]))

(trapperkeeper/defservice torrent-web-service
  [[:ConfigService get-in-config]
   [:WebroutingService add-ring-handler get-route]
   TorrentService]
  (init [this context]
    (log/info "Initializing torrent webservice")
    (let [url-prefix (get-route this)]
      (add-ring-handler
        this
        (compojure/context url-prefix []
          (core/app (tk-services/get-service this :TorrentService))))
      (assoc context :url-prefix url-prefix)))

  (start [this context]
         (let [host (get-in-config [:webserver :host])
               port (get-in-config [:webserver :port])
               url-prefix (get-route this)]
              (log/infof "Torrent web service started; visit http://%s:%s%s/test-file to check it out!"
                         host port url-prefix))
         context))
