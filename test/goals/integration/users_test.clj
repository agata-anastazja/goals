(ns goals.integration.users-test
  (:require
   [goals.users :as users]
   [clojure.test :refer :all]
   [next.jdbc :as jdbc]
   [goals.migrate :as migrate] 
   [next.jdbc.result-set :as rs]))

(deftest test-app
  (testing "create a user"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          _ (migrate/migrate uri)
          ds (jdbc/get-datasource {:jdbcUrl uri})]

      (with-open [conn (jdbc/get-connection ds {:auto-commit false})]
        (let [result (users/add {:parameters
                                 {:body {:username "Rahul"
                                         :password "Secretpassword"}}
                                 :ds conn})
              rows (jdbc/execute! conn ["select * from users"] {:builder-fn rs/as-unqualified-lower-maps})
              last-inserted-row (dissoc (last rows) :id)]
          (is (=  200 (:status result)))
          (is (= last-inserted-row  {:username "Rahul"
                                     :password "Secretpassword"}))))))
  (testing "only creates users with unique usernames"
    (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
          _ (migrate/migrate uri)
          ds (jdbc/get-datasource {:jdbcUrl uri})]
      (with-open [conn (jdbc/get-connection ds {:auto-commit false})]
        (let [_ (users/add {:parameters
                            {:body {:username "Rahul"
                                    :password "Secretpassword"}}
                            :ds conn})
              result (users/add {:parameters
                                 {:body {:username "Rahul"
                                         :password "Secretpassword"}}
                                 :ds conn})]
          (is (= (:status result)  409)))))))