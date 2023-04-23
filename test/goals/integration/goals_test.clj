(ns goals.integration.goals-test
  (:require
   [goals.goals :as core]
   [goals.migrate :as migrate]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.data.json :as json]))

(deftest test-app
  (testing "adding a yearly goal"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          _ (migrate/migrate uri)
          ds (jdbc/get-datasource {:jdbcUrl uri})
          parsed-goal (core/parse-goal {:description "Have fun doing serious side projects"
                                        :level 1
                                        :deadline "2023-01-01"})
          goal-id (:id parsed-goal)
          _ (core/save-goal parsed-goal
                            ds)
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})]
      (is (some (fn [row] (= (:id row) goal-id)) rows))

      (jdbc/execute-one! ds ["delete from goals where id = ?" goal-id])))

  (testing "getting a monthly goal with a yearly parent"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})
          parsed-goal (core/parse-goal {:description "Have fun doing serious side projects"
                                        :level 1
                                        :deadline "2023-01-01"})
          goal-id (:id parsed-goal)
          _ (core/save-goal parsed-goal
                            ds)
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})]
      (is (some (fn [row] (= (:id row) goal-id)) rows))

      (jdbc/execute-one! ds ["delete from goals where id = ?" goal-id])))
  (testing "getting a weekly goal" 
   (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
         ds (jdbc/get-datasource {:jdbcUrl uri})

         result (core/add {:parameters
                                {:body {:description "Have fun doing serious side projects"
                                        :level 1
                                        :deadline "2023-01-01"}}
                                :ds ds})
         rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
         last-inserted-row (last rows)]
     (is (= (:body result)  (json/write-str  {:id (:id last-inserted-row)})))))
  (testing "getting all goals" 
   (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
         ds (jdbc/get-datasource {:jdbcUrl uri})

         result (core/add {:parameters
                                {:body {:description "Have fun doing serious side projects"
                                        :level 1
                                        :deadline "2023-01-01"}}
                                :ds ds})
         rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
         last-inserted-row (last rows)]
     (is (= (:body result) (json/write-str {:id (:id last-inserted-row)}))))))


