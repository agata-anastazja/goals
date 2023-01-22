(ns goals.migrate
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(def config
  {:datastore  (jdbc/sql-database {:connection-uri "jdbc:sqlite:goals.db"})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate[]
    (repl/migrate config))