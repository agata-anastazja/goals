(ns goals.buddy-requests
  (:require
   [goals.persistance.buddy-requests :as persistance]
   [goals.persistance.users :as user-persistance]
   [goals.buddies :as buddies]
   [clojure.data.json :as json]
   [clojure.tools.logging :as log]
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
       :body   {:status "PENDING"
                :id buddy-request-id}})
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
       :body  {:buddy-requests received-buddy-requests}}) 
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))

;; What's in the request?
(defn accept [req]
  (try 
    (let [ds (:ds req)
          buddy-request-id (->
                            req
                            :parameters
                            :body
                            :buddy-request-id)
          user-id-1 (->
                     req
                     :parameters
                     :body
                     :user-id-1)
          user-id-2 (->
                     req
                     :parameters
                     :body
                     :user-id-2)]
      (persistance/accept buddy-request-id ds)
      (buddies/add (parse-uuid user-id-1) user-id-2 ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  {:status "ACCEPTED"}})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))

;; Either you could create some sort of wider "business logic" function that does the work of calling the DB functions in the right order etc

;; You probably want a function that does both these things for you
(defn business-logic []
      (persistance/accept buddy-request-id ds)
      (buddies/add (parse-uuid user-id-1) user-id-2 ds)
  )

(declare create-mock-db)

(defn accept-2 [ds buddy-request-id acceptee-user-id accepter-user-id])

(defn accept-2-test []
  (let [db (create-mock-db)
        buddy-request-id "123"
        ]
    (accept-2 db buddy-request-id "123" "456")
  ;; Doesn't return any data; it just updates the database, or otherwise throws an exception
    )
  )