(ns goals.users
  (:require
   [next.jdbc :as jdbc]
   [clojure.string :as str] 
   [next.jdbc.result-set :as rs]))

(defn save [{:keys [username password]} ds]
  (let [id (random-uuid)]
    (jdbc/execute-one! ds ["INSERT INTO users(id, username, password)
      values(?, ?, ?)" id username password])))

(defn get-all-users [ds]
  (jdbc/execute! ds ["SELECT * FROM users"]  {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-user [ds username password]
  (jdbc/execute-one! ds ["SELECT * FROM users WHERE username = ? AND password = ?" username password]))

(defn list-users [{ds :ds}]
   (try 
      (let [users (get-all-users ds)]
       {:status  200
        :body users})
    (catch Exception e
      (let [message (.getMessage e)]
          {:status 500
           :headers {"Content-Type" "text/html"}
           :body (str  "Can't get users! Get popular! error message: " message)}))))

(defn add [{{:keys [body]} :parameters ds :ds}] 
  (try
    (let [user body]
      (save user ds)
      {:status  200})
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
