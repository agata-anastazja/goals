(ns goals.buddy-requests
  (:require
   [goals.persistance.buddy-requests :as persistance]
   [goals.persistance.users :as user-persistance]
   [clojure.data.json :as json] 
   [goals.auth :as auth]))


(defn get-user-id [req]
  (let [{{:strs [authorization]} :headers} req
        [username password] (auth/decode-auth authorization)
        ds (:ds req)
        user (->>
              (user-persistance/get-user ds username password))]
    (:id user)))

(defn add [req]
  (try
    (let [requestee-id (->
                        req
                        :parameters
                        :body
                        :requestee-id
                        parse-uuid)
          ds (:ds req)
          requester-id (get-user-id req)]
      (persistance/save requestee-id requester-id ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:status "PENDING"})})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))

