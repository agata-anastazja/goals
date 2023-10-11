(ns goals.integration.goals-test
  (:require
   [goals.goals :as goals]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.data.json :as json]
   [goals.integration.test-utils :as test-utils]
   [goals.persistance.users :as user-persistance]))

(deftest add-goal-test
  (testing "add goal for a user"
    (with-open [conn (test-utils/create-connection)]
      (let [auth-header (test-utils/default-auth-header conn)
            req {:parameters {:body {:description "Have fun for a year"
                                     :level 3}}
                 :ds conn
                 :headers {"authorization" auth-header}}
            result (goals/add req)]
        (is (= 200 (:status result)))
        (is (uuid? (-> (json/read-json (:body result)) :id parse-uuid))))))
  

  (testing "adding a weekly goal with a non exsisting parent returns a 400"
    (with-open [conn (test-utils/create-connection)]
      (let [auth-header (test-utils/default-auth-header conn)
            goal {:parameters
                  {:body
                   {:description "Have fun doing serious side projects but differently"
                    :level 1
                    :goal-parent (random-uuid)}}
                  :ds conn
                  :headers {"authorization" auth-header}}
            result (goals/add goal)]
        (is (= (:status result) 400)))))
  (testing "Given a parent goal exists when we add a goal with a parent goal it saves successfully"
    (with-open [conn (test-utils/create-connection)]
      (let [auth-header (test-utils/default-auth-header conn)
            parent-req  {:parameters {:body {:description "Have fun doing serious side projects"
                                             :level 3}}
                         :ds conn
                         :headers {"authorization" auth-header}}
            parent-result (goals/add parent-req)
            parent-id (-> (json/read-json (:body parent-result)) :id parse-uuid)
            req  {:parameters {:body {:description "Have fun doing serious side projects"
                                      :level 2
                                      :goal-parent parent-id}}
                  :ds conn
                  :headers {"authorization" auth-header}}
            result (goals/add req)]
        (is (= (:status result) 200))))))

(deftest complete-goal-test
 (testing "completing a goal" 
   (with-open [conn (test-utils/create-connection)]
     (let [auth-header (test-utils/default-auth-header conn) 
           req {:parameters {:body {:description "Have fun for a year"
                                    :level 3}}
                :ds conn
                :headers {"authorization" auth-header}}
           result (goals/add req)
           goal-id (-> (json/read-json (:body result)) :id parse-uuid)
           _ (goals/complete {:parameters 
                              {:body {:id goal-id}}
                              :ds conn})
           result (goals/get-goal  {:path-params {:id goal-id}
                              :ds conn})]
       (is (= (:active (:body result)) false))))))

(deftest get-user-test
  (testing "get goals for a user"
    (with-open [conn (test-utils/create-connection)]
      (let [auth-header (test-utils/default-auth-header conn)
            req {:parameters {:body {:description "Rahuls has fun for a year"
                                     :level 3}}
                 :ds conn
                 :headers {"authorization" auth-header}}
            _ (goals/add req)

            user-req2 {:parameters {:body {:username "Agata"
                                           :password "secretsecret"}}
                       :ds conn}
            _ (test-utils/ensure-user user-req2)
            user2 (user-persistance/get-user conn "Agata" "secretsecret")
            auth-header2 (test-utils/auth-header user2)
            req2 {:parameters {:body {:description "Have fun for a year"
                                      :level 3}}
                  :ds conn
                  :headers {"authorization" auth-header2}}
            _ (goals/add req2)
            req3 {:parameters {:body {:level 3}}
                  :ds conn
                  :headers {"authorization" auth-header}}
            result (goals/get-all-goals req3)]
        (is (= 200 (:status result)))
        (is (= 1 (-> (:body result) count)))))))


(deftest test-get-all-goals-with-their-parents
  (testing "get all goals with their parents returns a goal and it's parent"
    (with-open [conn (test-utils/create-connection)]
      (let [auth-header (test-utils/default-auth-header conn)
            create-yearly-goal-req {:parameters {:body {:description "Have fun for a year"
                                                        :level 3}}
                                    :ds conn
                                    :headers {"authorization" auth-header}}
            yearly-goal-res (goals/add create-yearly-goal-req)
            parent-id (-> (json/read-json (:body yearly-goal-res)) :id parse-uuid)
            create-monthly-goal-req {:parameters {:body {:description "Have fun for a month"
                                                         :level 2
                                                         :goal-parent parent-id}}
                                     :ds conn
                                     :headers {"authorization" auth-header}}
            monthly-goal (goals/add create-monthly-goal-req)
            child-id (-> (json/read-json (:body monthly-goal)) :id parse-uuid)
            req {:parameters {:body {:level 2}}  
                 :ds conn
                 :headers {"authorization" auth-header}}
            result (goals/get-all-goals-with-their-parent req)
            first-result (first (:body result))]
        (is (= 200 (:status result)))
        (is (= 1 (-> (:body result) count)))
        (is (= child-id (-> first-result :id )))
        (is (= parent-id (-> first-result :goal-parent :id)))))))

