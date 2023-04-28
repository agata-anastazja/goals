(ns goals.users
  (:require
   [next.jdbc :as jdbc]
   [clojure.string :as str]))

(defn save [{:keys [username password]} ds]
  (let [id (random-uuid)]
    (jdbc/execute-one! ds ["INSERT INTO users(id, username, password)
      values(?, ?, ?)" id username password])))

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
