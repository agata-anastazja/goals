(ns goals.handler-test
  (:require 
    [clojure.test :refer :all]
    [ring.mock.request :as  mock] 
    [goals.handler :refer :all]))

(deftest test-app
  (testing "adding a yearly goal"
    (let [example-goal {:description "Feel pride in my work"
                        :level "yearly"}
        result  (add-goal example-goal )))]
      (is (= 200 (:status response)))
      (is (=  "successfully saved goal" (:body response))))))