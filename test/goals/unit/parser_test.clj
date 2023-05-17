(ns goals.unit.parser-test
  (:require [clojure.test :refer :all]
            [goals.parser :as parser]
            [java-time.api :as jt]))

(deftest parse-goal-test
  (testing "parses correctly a goal with no parent" 
    (let [current-time (jt/zoned-date-time 2023 1 1)

          deadline (jt/plus current-time (jt/days 7))

          params {:description "Have fun doing side projects"
                  :level 1}
          id (random-uuid)
          result (parser/parse params id current-time)]
      (is (= id (:id result)))
      (is (= (:created-at result) current-time))
      (is (= (:description params) (:description result)))
      (is (= nil (:goal-parent result)))
      (is (= 1 (:level result)))
      (is  (:active result))
      (is (= deadline (:deadline result))))))

(deftest calculate-deadline-test
  (testing "level 1 goal has a deadline a week from when it was set"
    (let [current-time (jt/zoned-date-time 2023 1 1)
          expected-result (jt/zoned-date-time 2023 1 8)]
      (is (= (parser/calculate-deadline current-time 1) expected-result))))
  (testing "level 2 goal has a deadline a month from when it was set"
    (let [current-time (jt/zoned-date-time 2023 1 1)
          expected-result (jt/zoned-date-time 2023 2 1)]
      (is (= (parser/calculate-deadline current-time 2) expected-result))))
  (testing "level 2 goal has a deadline a month from when it was set"
    (let [current-time (jt/zoned-date-time 2023 1 1)
          expected-result (jt/zoned-date-time 2024 1 1)]
      (is (= (parser/calculate-deadline current-time 3) expected-result)))))