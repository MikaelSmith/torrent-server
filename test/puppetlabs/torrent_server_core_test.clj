(ns puppetlabs.torrent-server-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.torrent-server-core :refer :all]))

(deftest hello-test
  (testing "says hello to caller"
    (is (= "Hello, foo!" (hello "foo")))))
