(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]
    [next.jdbc :as jdbc]))



(defn -main
  [& args]
  (migrate/migrate "jdbc:sqlite:goals.db")
  (jetty/run-jetty (handler/server (jdbc/get-datasource {:jdbcUrl "jdbc:sqlite:goals.db"}))
                 {:host   "0.0.0.0"
                  :port   8080
                  :join?  false
                  :async? true}))

