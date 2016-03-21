(ns puppetlabs.torrent-server-web-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.torrent-server-service :as torrent-svc]
            [puppetlabs.torrent-server-web-core :refer :all]
            [ring.mock.request :as mock]))

(deftest torrent-web-test
  (testing "says hello to caller"
    (let [torrent-service (reify torrent-svc/TorrentService
                            (torrent [this caller] (format "Testing, %s." caller)))
          ring-app (app torrent-service)
          response (ring-app (mock/request :get "/foo"))]
      (is (= 200 (:status response)))
      (is (= "Testing, foo." (:body response))))))
