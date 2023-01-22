(ns goals.handler-test
  (:require 
    [clojure.test :refer :all]
    [ring.mock.request :as  mock] 
    [goals.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response  (app 
                      ( ->
                        (mock/request :post "/")
                        (mock/json-body {:description "bar"})))]
      (is (= 200 (:status response)))
      (is (=  "successfully saved goal" (:body response))))))