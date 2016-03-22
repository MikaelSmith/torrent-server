(ns puppetlabs.torrent-server-web-service-test
  (:require [clojure.test :refer :all]
            [puppetlabs.test-helpers :refer :all]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-config]]
            [puppetlabs.trapperkeeper.services.webserver.jetty9-service :refer [jetty9-service]]
            [puppetlabs.trapperkeeper.services.webrouting.webrouting-service :refer [webrouting-service]]
            [clj-http.client :as client]
            [puppetlabs.torrent-server-service :as svc]
            [puppetlabs.torrent-server-web-service :as web-svc]))

(deftest torrent-web-service-test
  (testing "returns a torrent for known file"
    (with-app-with-config app
      [svc/torrent-service
       web-svc/torrent-web-service
       jetty9-service
       webrouting-service]
      {:webserver {:host "localhost"
                   :port 8080}
       :web-router-service {
         :puppetlabs.torrent-server-web-service/torrent-web-service "/torrent"}
       :torrent-server {:file-source "dev-resources"}}
      (let [resp (client/get "http://localhost:8080/torrent/test-file" {:as :text})]
        (is (= (slurp-torrent "dev-resources/test-file.torrent") (normalize-torrent (:body resp)))))))
  (testing "returns 404 for unknown file"
    (with-app-with-config app
      [svc/torrent-service
       web-svc/torrent-web-service
       jetty9-service
       webrouting-service]
      {:webserver {:host "localhost"
                   :port 8080}
       :web-router-service {
         :puppetlabs.torrent-server-web-service/torrent-web-service "/torrent"}
       :torrent-server {:file-source "dev-resources"}}
      (is (thrown-with-msg? Exception #"404"
                   (client/get "http://localhost:8080/torrent/unknown-file")))))
  )
