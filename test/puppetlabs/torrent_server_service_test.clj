(ns puppetlabs.torrent-server-service-test
  (:require [clojure.test :refer :all]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-empty-config]]
            [puppetlabs.torrent-server-service :as svc]))

(deftest torrent-service-test
  (testing "says hello to caller"
    (with-app-with-empty-config app [svc/torrent-service]
      (let [torrent-service (app/get-service app :TorrentService)]
        (is (= "Hello, foo!" (svc/torrent torrent-service "foo")))))))
