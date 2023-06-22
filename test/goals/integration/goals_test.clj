(ns goals.integration.goals-test
  (:require
   [goals.goals :as goals]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.data.json :as json]
   [goals.integration.test-utils :as test-utils]))

(deftest test-app
  (testing "adding a weekly goal"
   (with-open [conn (test-utils/create-connection)]
    (let [req {:parameters {:body {:description "Have fun doing serious side projects"
                                   :level 1}}
               :ds conn}
          result (goals/add req)]
      (is (= (:status result) 200))
      (is (uuid? (-> (json/read-json (:body result)) :id parse-uuid)))))))

(deftest test-goal-handler 
 (testing "adding a weekly goal with a non exsisting parent returns a 400"
   (with-open [conn (test-utils/create-connection)]
     (let [goal {:parameters
                 {:body
                  {:description "Have fun doing serious side projects but differently"
                   :level 1
                   :goal-parent (random-uuid)}}
                 :ds conn}
           result (goals/add goal)]
       (is (= (:status result) 400)))))

   (testing "Given a parent goal exists when we add a goal with a parent goal it saves successfully"
     (with-open [conn (test-utils/create-connection)]
      (let [parent-req  {:parameters {:body {:description "Have fun doing serious side projects"
                                             :level 3}}
                         :ds conn}
            parent-result (goals/add parent-req)
            parent-id (-> (json/read-json (:body parent-result)) :id parse-uuid) 
            req  {:parameters {:body {:description "Have fun doing serious side projects"
                                      :level 3
                                      :goal-parent parent-id}}
                  :ds conn}
            result (goals/add req)]
        (is (= (:status result) 200)))))

  (testing "completing a goal" 
    (with-open [conn (test-utils/create-connection)]
      (let [
            _ (goals/add {:parameters
                          {:body {:description "Have fun doing serious side projects"
                                  :level 1}}
                          :ds conn})
            rows (jdbc/execute! conn ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
            last-inserted-row (last rows)
            id (:id last-inserted-row)
            _ (goals/complete {:parameters 
                               {:body {:id id}}
                               :ds conn})
            rows (jdbc/execute! conn ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
            completed (first (filterv (fn [row] (= (:id row) id)) rows))]
        (is (= (:active completed) false)))))
  
  (testing "getting a goal"
    (with-open [conn (test-utils/create-connection)]
      (let [req {:parameters
                 {:body {:description "have fun this week"
                         :level 1}}
                 :ds conn}
            _ (goals/add req)
            rows (jdbc/execute! conn ["select * from goals"] {:builder-fn rs/as-unqualified-lower-maps})
            last-inserted-row (last rows)
            id (:id last-inserted-row)
            result (goals/get-goal {:path-params {:id id}
                                    :ds conn})]
        (is (= (-> result :body :goal) (-> req :parameters :body :description)))))))


(deftest test-get-all-goals
  (testing "get all goals returns 2 created goals"
    (with-open [conn (test-utils/create-connection)]
      (let [create-req {:parameters {:body {:description "Have fun"
                                            :level 1}}
                        :ds conn}
            _ (goals/add create-req)
            _ (goals/add create-req)
            req {:parameters {:body {:level 1}}  :ds conn}
            result (goals/get-all-goals req)] 
        (is (= 200 (:status result)))
        (is (= 2 (-> (:body result) count)))))))

(deftest test-get-all-goals-with-their-parents
  (testing "get all goals returns 2 created goals"
    (with-open [conn (test-utils/create-connection)]
      (let [create-yearly-goal-req {:parameters {:body {:description "Have fun for a year"
                                            :level 3}}
                        :ds conn}
            yearly-goal-res (goals/add create-yearly-goal-req)
            parent-id (-> (json/read-json (:body yearly-goal-res)) :id parse-uuid)
            create-monthly-goal-req {:parameters {:body {:description "Have fun for a month"
                                                         :level 2
                                                         :goal-parent parent-id}}
                                     :ds conn}
            monthly-goal (goals/add create-monthly-goal-req)
            child-id (-> (json/read-json (:body monthly-goal)) :id parse-uuid)
            req {:parameters {:body {:level 2}}  :ds conn}
            result (goals/get-all-goals-with-their-parent req)]
        (is (= 200 (:status result)))
        (is (= 1 (-> (:body result) count)))))))