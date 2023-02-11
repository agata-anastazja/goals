(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]
    [next.jdbc :as jdbc]))


(defn -main
  [& args]
  (let [db-host (or (System/getenv "DB_HOST") "127.0.0.1")
        db-port (or (System/getenv "DB_PORT") "5432")
        db-user (or (System/getenv "DB_USER") "goals")
        db-password (or (System/getenv "DB_PASSWORD") "goals")
        connection-url (format "jdbc:postgresql://%s:%s/goals?user=%s&password=%s" 
                         db-host db-port db-user db-password)]
   (migrate/migrate connection-url )
   (jetty/run-jetty (handler/server (jdbc/get-datasource {:jdbcUrl connection-url}))
                    {:host   "0.0.0.0"
                     :port   8080
                     :join?  false
                     :async? true})))

