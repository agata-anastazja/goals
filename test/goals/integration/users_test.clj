(ns goals.integration.users-test
  (:require
   [goals.users :as users]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]))

(deftest test-app
  (testing "create a user"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})

          result (users/add {:parameters
                                  {:body {:username "Rahul"
                                          :password "Secretpassword"}}
                                  :ds ds})
          rows (jdbc/execute! ds ["select * from users"] {:builder-fn rs/as-unqualified-lower-maps})
          last-inserted-row (dissoc (last rows) :id)]
      (is (= (:status result)  200))
      (is (= last-inserted-row  {:username "Rahul"
                                          :password "Secretpassword"}))))
  (testing "only creates users with unique usernames"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          ds (jdbc/get-datasource {:jdbcUrl uri})
          result (users/add {:parameters
                                  {:body {:username "Rahul"
                                          :password "Secretpassword"}}
                                  :ds ds})
         ]
      (is (= (:status result)  409)))))