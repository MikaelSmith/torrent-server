(ns puppetlabs.torrent-server-web-core
  (:require [puppetlabs.torrent-server-service :as torrent-svc]
            [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [compojure.route :as route]))

(defn app
  [torrent-service]
  (compojure/routes
    (compojure/GET "/:caller" [caller]
      (fn [req]
        (log/info "Handling request for caller:" caller)
        {:status  200
         :headers {"Content-Type" "text/plain"}
         :body    (torrent-svc/torrent torrent-service caller)}))
    (route/not-found "Not Found")))
