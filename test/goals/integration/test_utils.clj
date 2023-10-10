(ns goals.integration.test-utils
  (:require [next.jdbc :as jdbc]
            [goals.migrate :as migrate]
            [goals.users :as users])
  (:import [java.util Base64]))


(defn create-connection []
  (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
        _ (migrate/migrate uri)
        ds (jdbc/get-datasource {:jdbcUrl uri})]
    (jdbc/get-connection ds {:auto-commit false})))


(defn ensure-user [req]
  (users/add req))


(defn auth-header [user]
  (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (str (:username user) ":" (:password user))))))


(defn default-auth-header[conn]
  (let [user {:username "RahulUnique"
              :password "secretsecret"}
        user-req {:params user
                  :ds conn}
        _ (ensure-user user-req)]
    (auth-header user)))