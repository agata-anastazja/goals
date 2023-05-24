(ns goals.integration.goals-test
  (:require
   [goals.goals :as goals]
   [goals.migrate :as migrate]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]))

(deftest test-app
  (testing "adding a weekly goal"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          _ (migrate/migrate uri)
          ds (jdbc/get-datasource {:jdbcUrl uri})
          req {:parameters {:body {:description "Have fun doing serious side projects"
                                   :level 1}}
               :ds ds}
          result (goals/add req)]
      (is (= (:status result) 200))))
  
 (testing "adding a weekly goal with a non exsisting parent returns a 400"
   (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
         _ (migrate/migrate uri)
         ds (jdbc/get-datasource {:jdbcUrl uri})
         goal {:parameters 
               {:body
                {:description "Have fun doing serious side projects but differently"
                 :level 1
                 :goal-parent (random-uuid)}}
               :ds ds}
         result (goals/add goal)] 
     (is (= (:status result) 400))))

  ;;  (testing "adding a monthly goal that is an extension of a yearly goal"
  ;;    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
  ;;          ds (jdbc/get-datasource {:jdbcUrl uri})
  ;;          parent-goal (parser/parse {:description "Have fun doing serious side projects"
  ;;                                     :level 3})
  ;;          parent-goal-id (:id parent-goal)
  ;;          _ (goals/save-goal parent-goal
  ;;                             ds)
  ;;          goal (parser/parse {:description "Have fun doing serious side projects"
  ;;                              :level 2
  ;;                              :goal-parent parent-goal-id}) 
  ;;          goal-id (:id goal)

  ;;          _ (goals/save-goal goal
  ;;                             ds)
  ;;          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})]
  ;;      (is (some (fn [row] (= (:id row) goal-id)) rows))

  ;;      (jdbc/execute-one! ds ["delete from goals where id = ?" goal-id])))

  (testing "completing a goal" 
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})

          _ (goals/add {:parameters
                        {:body {:description "Have fun doing serious side projects"
                                :level 1}}
                        :ds ds})
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
          last-inserted-row (last rows)
          id (:id last-inserted-row)
          _ (goals/complete {:parameters 
                             {:body {:id id}}
                             :ds ds})
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
          completed (first (filterv (fn [row] (= (:id row) id)) rows))]
      (is (= (:active completed) false))))
  
  (testing "getting a goal"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})
          req {:parameters
               {:body {:description "have fun this week"
                       :level 1}}
               :ds ds}
          _ (goals/add req)
          rows (jdbc/execute! ds ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
          last-inserted-row (last rows)
          id (:id last-inserted-row)
          result (goals/get-goal {:path-params {:id id}
                                  :ds ds})]
      (is (= (-> result :body :goal) (-> req :parameters :body :description))))))


