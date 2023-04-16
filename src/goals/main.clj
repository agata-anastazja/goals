(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]
    [next.jdbc :as jdbc]))


(defn -main
  [& args]
  (let [connection-url (or (System/getenv "DB_JDBC_URI") "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals")]
    (println connection-url)
    (migrate/migrate connection-url)
    (jetty/run-jetty (handler/server (jdbc/get-datasource {:jdbcUrl connection-url}))
                     {:host   "0.0.0.0"
                      :port   80
                      :join?  false
                      :async? true})))

