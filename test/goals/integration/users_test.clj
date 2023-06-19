(ns goals.integration.users-test
  (:require
   [goals.users :as users]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [goals.integration.test-utils :as test-utils]
   [next.jdbc.result-set :as rs]))


(deftest test-app
  (testing "create a user"
    (with-open [conn (test-utils/create-connection)]
      (let [result (users/add {:parameters
                               {:body {:username "Rahul"
                                       :password "Secretpassword"}}
                               :ds conn})
            rows (jdbc/execute! conn ["select * from users"] {:builder-fn rs/as-unqualified-lower-maps})
            last-inserted-row (dissoc (last rows) :id)]
        (is (=  200 (:status result)))
        (is (= last-inserted-row  {:username "Rahul"
                                   :password "Secretpassword"})))))
  (testing "only creates users with unique usernames" 
    (with-open [conn (test-utils/create-connection)]
      (let [_ (users/add {:parameters
                          {:body {:username "Rahul"
                                  :password "Secretpassword"}}
                          :ds conn})
            result (users/add {:parameters
                               {:body {:username "Rahul"
                                       :password "Secretpassword"}}
                               :ds conn})]
        (is (= (:status result)  409))))))