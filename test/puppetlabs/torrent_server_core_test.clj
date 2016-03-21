(ns puppetlabs.torrent-server-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.torrent-server-core :refer :all]))

(deftest torrent-test
  (testing "says hello to caller"
    (is (= "Hello, foo!" (get-torrent :tracker "foo")))))
