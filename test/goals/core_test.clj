(ns goals.core-test
  (:require [clojure.test :refer :all]
            [goals.core :as goal]))

;; TODO: determine the right way to test this
;;  :deadline deadline date from string
;;  :created-at created-at now date
;;  :last-updated created-at

(deftest parse-goal-test
  (testing "parses correctly a goal with no parent" 
    (let [params {:description "Have fun doing side projects"
                  :level 1
                  :deadline "2023-01-01"}
          result (goal/parse-goal params)]
      (is (uuid? (:id result)))
      (is (= (:description params) (:description result)))
      (is (= nil (:goal-parent result)))
      (is (= 1 (:level result)))
      (is  (:active result))
      #_(is (= 1 (:deadline result))))))