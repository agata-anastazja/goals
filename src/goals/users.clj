(ns goals.users
  (:require
   [goals.persistance.users :as persistance]
   [clojure.string :as str]
   [clojure.data.json :as json]))

(defn user-exists? [ds username password]
  (some? (persistance/get-user ds username password)))

(defn add [{{:keys [body]} :parameters ds :ds}] 
  (try
    (let [user-id  (random-uuid)
          user (assoc body :user-id user-id )]
      (persistance/save user ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:id user-id})})
    (catch Exception e
      (let [message (.getMessage e)]
        (cond
          (str/includes? message "duplicate key value violates unique constraint")
          {:status 409
           :headers {"Content-Type" "text/html"}
           :body "Goal not saved! Username not unique!"}
          :else
          {:status 500
           :headers {"Content-Type" "text/html"}
           :body (str  "Goal not saved! Save yourself! error message: " message)})))))
