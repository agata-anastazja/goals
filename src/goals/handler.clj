(ns goals.handler
    (:require  
        [goals.core :as core]
        [ring.middleware.json :as middleware]
        [ring.middleware.params :refer [wrap-params]]
        [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
        [ring.handler.dump        :refer [handle-dump]]
        [compojure.core :refer :all]))


(defroutes  app-routes 
    (GET "/" []
     {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Pew pew!"})
    (POST "/" req (core/add-goal (:body req))))

(def app
    (->  app-routes
        (middleware/wrap-json-body  {:keywords? true })
        wrap-params
        middleware/wrap-json-response
        (wrap-defaults api-defaults)))