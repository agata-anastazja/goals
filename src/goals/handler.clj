(ns goals.handler
    (:require  
        [goals.core :as core]
        [compojure.core :refer :all]
        [org.httpkit.server :as server]))


(defroutes  app-routes 

    (GET "/" []
     {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Pew pew!"})
    (POST "/" req
        
            (prn (:params req))
            (core/add-goal (-> req :body :description))))