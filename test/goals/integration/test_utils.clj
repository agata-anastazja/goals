(ns goals.integration.test-utils
  (:require [next.jdbc :as jdbc]
            [goals.migrate :as migrate]))


(defn create-connection []
  (let [uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals"
        _ (migrate/migrate uri)
        ds (jdbc/get-datasource {:jdbcUrl uri})]
    (jdbc/get-connection ds {:auto-commit false})))