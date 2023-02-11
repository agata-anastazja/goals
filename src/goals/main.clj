(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]
    [next.jdbc :as jdbc]))


(defn -main
  [& args]
  (let [connection-url (System/getenv "DB_JDBC_URI")]
   (migrate/migrate connection-url )
   (jetty/run-jetty (handler/server (jdbc/get-datasource {:jdbcUrl connection-url}))
                    {:host   "0.0.0.0"
                     :port   8080
                     :join?  false
                     :async? true})))

