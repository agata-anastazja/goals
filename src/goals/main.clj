(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [ring.adapter.jetty :as jetty]))



(defn -main
  [& args]
  (jetty/run-jetty (var handler/server)
                 {:host   "0.0.0.0"
                  :port   8080
                  :join?  false
                  :async? true}))

