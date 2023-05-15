(ns goals.unit.goals-test
  (:require [clojure.test :refer :all]
            [goals.goals :as core]))
;; TODO: 
;;  :deadline deadline date from string
;;  :created-at created-at now date
;;  :last-updated created-at

(deftest parse-goal-test
  (testing "parses correctly a goal with no parent" 
    (let [params {:description "Have fun doing side projects"
                  :level 1
                  :deadline "2023-01-01"}
          now (core/now)
          id (random-uuid)
          result (core/parse-goal params id now)]
      (is (= id (:id result)))
      (is (= (:created-at result) now))
      (is (= (:description params) (:description result)))
      (is (= nil (:goal-parent result)))
      (is (= 1 (:level result)))
      (is  (:active result))
      #_(is (= 1 (:deadline result))))))