(ns puppetlabs.torrent-server-web-core
  (:require [puppetlabs.torrent-server-service :as torrent-svc]
            [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [compojure.route :as route]))

(defn app
  [torrent-service]
  (compojure/routes
    (compojure/GET "/:file" [file]
      (fn [req]
        (log/info "Handling request for file:" file)
        (if-let [torrent (torrent-svc/torrent torrent-service file)]
          {:status  200
           :headers {"Content-Type" "application/x-bittorrent"}
           :body    torrent}
          {:status 404
           :body "Requested file unavailable"})))
    (route/not-found "Not Found")))
