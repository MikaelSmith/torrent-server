(ns puppetlabs.torrent-server-web-service-test
  (:require [clojure.test :refer :all]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-config]]
            [puppetlabs.trapperkeeper.services.webserver.jetty9-service :refer [jetty9-service]]
            [puppetlabs.trapperkeeper.services.webrouting.webrouting-service :refer [webrouting-service]]
            [clj-http.client :as client]
            [puppetlabs.torrent-server-service :as svc]
            [puppetlabs.torrent-server-web-service :as web-svc]))

(deftest torrent-web-service-test
  (testing "says hello to caller"
    (with-app-with-config app
      [svc/torrent-service
       web-svc/torrent-web-service
       jetty9-service
       webrouting-service]
      {:webserver {:host "localhost"
                   :port 8080}
       :web-router-service {
         :puppetlabs.torrent-server-web-service/torrent-web-service "/torrent"}}
      (let [resp (client/get "http://localhost:8080/torrent/foo" {:as :text})]
        (is (= "Hello, foo!" (:body resp)))))))
