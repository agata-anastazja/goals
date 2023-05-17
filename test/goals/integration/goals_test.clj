(ns goals.integration.goals-test
  (:require
   [goals.goals :as goals]
   [goals.migrate :as migrate]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.data.json :as json]
   [goals.parser :as parser]))

(deftest test-app
  (testing "adding a yearly goal"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          _ (migrate/migrate uri)
          ds (jdbc/get-datasource {:jdbcUrl uri})
          parsed-goal (parser/parse {:description "Have fun doing serious side projects"
                                         :level 1
                                         :deadline "2023-01-01"})
          goal-id (:id parsed-goal)
          _ (goals/save-goal parsed-goal
                             ds)
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})]
      (is (some (fn [row] (= (:id row) goal-id)) rows))

      (jdbc/execute-one! ds ["delete from goals where id = ?" goal-id])))
 
  (testing "completing a goal" 
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})

          _ (goals/add {:parameters
                        {:body {:description "Have fun doing serious side projects"
                                :level 1
                                :deadline "2023-01-01"}}
                        :ds ds})
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
          last-inserted-row (last rows)
          id (:id last-inserted-row)
          _ (goals/complete {:parameters 
                             {:body {:id id}}
                             :ds ds})
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
          completed (first (filterv (fn [row] (= (:id row) id)) rows))]
      (is (= (:active completed) false)))))


