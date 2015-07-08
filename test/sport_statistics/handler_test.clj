(ns sport-statistics.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [sport-statistics.handler :refer :all]))

(deftest test-app
  (testing "main tennis route"
    (let [response (app (mock/request :get "/statistics/tennis"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{\"id\":\"tennis\"}"))))

  (testing "not-found at main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 404)))))
