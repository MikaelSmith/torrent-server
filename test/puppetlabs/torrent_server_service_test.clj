(ns puppetlabs.torrent-server-service-test
  (:require [clojure.test :refer :all]
            [puppetlabs.test-helpers :refer :all]
            [puppetlabs.trapperkeeper.app :as app]
            [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-config
                                                                  with-app-with-empty-config]]
            [puppetlabs.torrent-server-service :as svc]))

(deftest torrent-service-test
  (testing "fails to start without config"
    (is (thrown-with-msg? Exception #"torrent-server requires file-source option be configured"
                          (with-app-with-empty-config app [svc/torrent-service]
                            (app/get-service app :TorrentService)))))
  (testing "starts with file-source configured"
    (with-app-with-config app [svc/torrent-service]
      {:torrent-server {:file-source "dev-resources"}}
      (let [torrent-service (app/get-service app :TorrentService)
            expected (slurp-torrent "dev-resources/test-file.torrent")
            actual (slurp-torrent (svc/torrent torrent-service "test-file"))]
        (is (= expected actual)))))
  )
