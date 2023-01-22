(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]))



(defn -main
  [& args]
  (migrate/migrate)
  (jetty/run-jetty (var handler/server)
                 {:host   "0.0.0.0"
                  :port   8080
                  :join?  false
                  :async? true}))

