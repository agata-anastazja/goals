(ns goals.handler
    (:require  
        [goals.core :as core]
        [ring.middleware.json :as middleware]
        [ring.middleware.params :refer [wrap-params]]
        [ring.middleware.defaults :refer [api-defaults wrap-defaults]]

        [compojure.handler :as handler]
        [compojure.core :refer :all]
        [org.httpkit.server :as server]))


(defroutes  app-routes 

    (GET "/" []
     {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Pew pew!"})
    (POST "/" [] core/add-goal ))

(def app
    (-> (handler/api app-routes)
        (middleware/wrap-json-body  {:keywords? true })
        wrap-params
        middleware/wrap-json-response
        (api-defaults wrap-defaults)))