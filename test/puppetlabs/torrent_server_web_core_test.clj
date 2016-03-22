(ns puppetlabs.torrent-server-web-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.torrent-server-service :as torrent-svc]
            [puppetlabs.torrent-server-web-core :refer :all]
            [ring.mock.request :as mock]))

(deftest torrent-web-test
  (testing "returns 200 for known file"
    (let [torrent-service (reify torrent-svc/TorrentService
                            (torrent [this file] (format "Testing, %s." file)))
          ring-app (app torrent-service)
          response (ring-app (mock/request :get "/known-file"))]
      (is (= 200 (:status response)))
      (is (= "Testing, known-file." (:body response)))))
  (testing "returns 404 for unknown file"
    (let [torrent-service (reify torrent-svc/TorrentService
                            (torrent [this file] nil))
          ring-app (app torrent-service)
          response (ring-app (mock/request :get "/unknown-file"))]
      (is (= 404 (:status response)))
      (is (= "Requested file unavailable"))))
  )
