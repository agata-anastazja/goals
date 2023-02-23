(ns goals.integration-test
  (:require 
    [goals.core :as core]
    [goals.migrate :as migrate]
    [clojure.test :refer :all]
    [next.jdbc :as jdbc]
    [next.jdbc.result-set :as rs]
    [clojure.java.io :as io]))

;; (deftest test-app
;;   (testing "adding a yearly goal"
;;     (migrate/migrate "jdbc:sqlite:goals-test.db")
;;    (let [ds (jdbc/get-datasource {:jdbcUrl "jdbc:sqlite:goals-test.db"})
;;           example-request {
;;             :ds ds
;;             :parameters {:body {:description "Feel pride in my work"
;;                         :level "yearly"}}}
;;         result  (core/add-goal example-request )
;;         rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
;;         goal (first rows)]
;;         (prn rows)
;;         (is (= {:goal "Feel pride in my work" :goal_level "yearly"} goal))
        
;;       (is (= 200 (:status result))))
;;       (io/delete-file "goals-test.db")))

;; run migration
;; run core/add-goal
;; run core/get-goal

(comment
  (def uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals")
  ;; (def uri "jdbc:sqlite:goals-test.db") 
  (migrate/migrate uri) 
  (migrate/rollback uri)
  (def ds (jdbc/get-datasource {:jdbcUrl uri})) 
  (def parsed-goal (core/parse-goal {:description "Have fun doing side projects"
                                     :level 1
                                     :deadline "2023-01-01"}))
  (core/save-goal parsed-goal
                  ds)
  (def rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps}))
  rows
  ;; (core/get-goal)
  )