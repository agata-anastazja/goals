(ns goals.handler
  (:require
   [goals.goals :as goals]
   [goals.users :as users] 
   [goals.buddy-requests :as buddy-requests]
   [goals.auth :as auth]
   [goals.ui :as ui]
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.http :as http]
   [reitit.coercion.malli :as malli]
   [reitit.http.coercion :as coercion]
   [reitit.http.interceptors.parameters :as parameters]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [reitit.http.interceptors.exception :as exception]
   [reitit.interceptor.sieppari :as sieppari]
   [sieppari.context :as sie-context]))


(defn system-interceptor
  [ds]
  {:enter #(assoc-in % [:request :ds] ds)})

(def params->keywords-interceptor
  {:enter (fn[{{params :params} :request :as ctx}]
            (assoc-in ctx [:request :params] (into {} (map (fn [[k v]] [(keyword k) v]) params))))})

(def auth-interceptor
  {:enter (fn [{{{:strs [authorization]} :headers
                 ds :ds} :request :as ctx}]
            (if-not (auth/authorised? ds authorization)
              (sie-context/terminate
               ctx
               {:status 401
                :body {:message "Not authorised"}
                :content-type "application/json"})
              ctx))})

(defn routes []
  [["/" {:get
         {:handler ui/welcome}
         :post {:handler ui/welcome}}]
   
  ["/users"
    {:post {:handler users/add
            :interceptors [params->keywords-interceptor]
            :parameters {:form [:map {:closed false}
                                     [:username :string]
                                     [:password :string]]}}}]
   ["/buddy-requests" {:post {:handler buddy-requests/add 
                              :parameters {:body [:map {:closed false}
                                                  [:requestee-id :string]]}}
                       :get {:handler buddy-requests/get-received-requests
                             :interceptors [auth-interceptor]}}]
   ["/goals" {:get {:handler goals/get-all-goals
                    :interceptors [auth-interceptor]
                    :parameters {:body [:map {:closed false}
                                        [:level :int]]}}
              :post {:handler goals/add
                     :interceptors [auth-interceptor]
                     :parameters {:body [:map {:closed false}
                                         [:description :string]
                                         [:level :int]
                                         [:goal-parent {:optional true} :string]]}}}]

   ["/goals/:id" {:get goals/get-goal
                  :interceptors [auth-interceptor]}]])

(defn server
  [ds]
  (println "starting server")
  (http/ring-handler
   (http/router (routes)
                {:data {#_#_:coercion     malli/coercion
                        #_#_:muuntaja     m/instance
                        :interceptors [;; query params -> request map
                                       (parameters/parameters-interceptor)
                            ;; verify format of data
                                       (muuntaja/format-negotiate-interceptor)
                            ;; verify response data
                                       (muuntaja/format-response-interceptor)
                            ;; human readable error
                                       (exception/exception-interceptor)
                            ;; encoding
                                       (muuntaja/format-request-interceptor)
                            ;; coerce different bit of the response ie "1" to 1, json to map
                                       (coercion/coerce-exceptions-interceptor)
                                       (coercion/coerce-response-interceptor)
                                       (coercion/coerce-request-interceptor)
                                       (system-interceptor ds)]}})
   (ring/routes
    (ring/create-default-handler
     {:not-found (constantly {:status  200
                              :headers {"Content-Type" "application/json"}
                              :body    "{\"message\": \"Took a wrong turn?\"}"})}))
   {:executor sieppari/executor}))
