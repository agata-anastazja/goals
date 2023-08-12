(ns goals.buddies
   (:require
    [goals.persistance.buddies :as persistance]
    [clojure.data.json :as json]
    [goals.auth :as auth]))


(defn get-buddies[req]
  (try
    (do
      (let [user-id (auth/get-user-id req)
            results (persistance/get-buddies user-id (:ds req))
            buddies-ids (map #(get % :user_id_2) results)] 
        {:status  200
         :headers {"Content-Type" "application/json"}
         :body  (json/write-str {:buddies buddies-ids})}))
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))


(defn add [user-id-1 user-id-2 ds]
  (try
    (do
    ;;   todo: within a single transaction refactor
    ;;   todo: validate that user-id-1 and user-id-2 are not already buddies
    ;;   todo: validate that user-id-1 and user-id-2 are not the same user
    ;;   todo: validate that user-id-1 and user-id-2 exist
      (persistance/save user-id-1 user-id-2 ds)
      (persistance/save user-id-2 user-id-1 ds) 
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:status "PENDING"})})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))