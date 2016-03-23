(ns puppetlabs.torrent-server-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.test-helpers :refer :all]
            [puppetlabs.torrent-server-core :refer :all]))

(deftest torrent-test
  (testing "returns a torrent for known file"
    (with-tracker
      (let [expected (slurp-torrent "dev-resources/test-file.torrent")
            actual (slurp-torrent (get-torrent tracker "dev-resources" "test-file"))]
        (is (= expected actual))
        )
      (let [trackedTorrents (.getTrackedTorrents tracker)]
        (is (= 1 (count trackedTorrents)))
        ;(is (= 1 (.seeders (first trackedTorrents))))) ; Can't test until we can access Clients, seeding started asynchronously
        )))
  (testing "returns nil if file not found"
    (with-tracker
      (is (nil? (get-torrent tracker "dev-resources" "a-file")))
      ))
  (testing "returns nil if unsafe file path requested"
    (with-tracker
      (is (nil? (get-torrent tracker "dev-resources" "../dev-resources/test-file")))
      ))
  )
