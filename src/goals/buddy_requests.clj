(ns goals.buddy-requests
  (:require
   [goals.persistance.buddy-requests :as persistance]
   [goals.persistance.users :as user-persistance]
   [goals.buddies :as buddies]
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
    (let [buddy-request-id (random-uuid)
          requestee-id (->
                        req
                        :parameters
                        :body
                        :requestee-id
                        parse-uuid)
          ds (:ds req)
          requester-id (get-user-id req)]
      (persistance/save buddy-request-id requestee-id requester-id ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:status "PENDING"})})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))

(defn get-received-requests [req]
  (try
    (let [ds (:ds req)
          requestee-id (get-user-id req)
          received-buddy-requests (persistance/get-received-requests requestee-id ds)]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:buddy-requests received-buddy-requests})})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))

(defn accept [req]
  (try 
    (let [ds (:ds req)
          buddy-request-id (->
                            req
                            :parameters
                            :body
                            :buddy-request-id
                            parse-uuid)
          user-id-1 (->
                     req
                     :parameters
                     :body
                     :user-id-1
                     parse-uuid)
          user-id-2 (->
                     req
                     :parameters
                     :body
                     :user-id-2
                     parse-uuid)]
      (persistance/accept buddy-request-id ds)
      (buddies/add user-id-1 user-id-2 ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:status "ACCEPTED"})})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))