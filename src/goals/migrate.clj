(ns goals.migrate
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(defn config [url]
  {:datastore  (jdbc/sql-database {:connection-uri url})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate[url]
    (repl/migrate (config url)))