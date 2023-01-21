(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [org.httpkit.server :as server]))

(defn -main
  [& args]
  (server/run-server #'handler/app-routes {:port 8080}))

