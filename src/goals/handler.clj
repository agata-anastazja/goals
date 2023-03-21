(ns goals.handler
  (:require
   [goals.core :as core]
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.http :as http]
   [reitit.coercion.malli :as malli]
   [reitit.http.coercion :as coercion]
   [reitit.http.interceptors.parameters :as parameters]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [reitit.http.interceptors.exception :as exception]
   [reitit.interceptor.sieppari :as sieppari]))


(defn system-interceptor
  [ds]
  {:enter #(assoc-in % [:request :ds] ds)})

(def routes
  [["/add" {:post core/add-goal
            :parameters {:body [:map {:closed false}
                                [:description :string]
                                [:level :int]
                                [:deadline :string]
                                [:goal-parents {:optional true} [:vector :string]]]}}]
   ["/goals/:id" {:get core/get-goal}]])

(defn server
  [ds]
  (http/ring-handler
   (http/router routes
                {:data {:coercion     malli/coercion
                        :muuntaja     m/instance
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
     {:not-found (constantly {:status  404
                              :headers {"Content-Type" "application/json"}
                              :body    "{\"message\": \"Took a wrong turn?\"}"})}))
   {:executor sieppari/executor}))