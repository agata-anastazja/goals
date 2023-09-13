(ns goals.users
  (:import [java.util Base64])
  (:require
   [goals.persistance.users :as persistance]
   [clojure.string :as str]))

(defn user-exists? [ds username password]
  (some? (persistance/get-user ds username password)))

(defn add [{params :params ds :ds :as req}]
  (try
    (let [user-id  (random-uuid)
          user (assoc params :user-id user-id)]
      (persistance/save user ds)
      {:status  302
       :headers {"Location" "/post-sign-up"}})
    (catch Exception e
      (let [message (.getMessage e)]
        (cond
          (str/includes? message "duplicate key value violates unique constraint")
          {:status 409
           :headers {"Content-Type" "text/html"}
           :body "User not created! Username not unique!"}
          :else
          {:status 500
           :headers {"Content-Type" "text/html"}
           :body (str  "User not created! Save yourself! error message: " message)})))))



(defn auth-header [username password]
  (str "Basic " (.encodeToString (Base64/getEncoder) (.getBytes (str username ":" password)))))



(defn log-in [{params :params ds :ds :as req}]
    (try
      (let [username (:username params)
            password (:password params)
            user-exists? (user-exists? ds username password)
            auth-header (auth-header username password)]
     
        {:status  302
         :authorization auth-header
         :headers {"Location" "/"
                   }})
      (catch Exception e
        (let [message (.getMessage e)] 
          {:status 500
           :headers {"Content-Type" "text/html"}
           :body (str  "error message: " message)}))))