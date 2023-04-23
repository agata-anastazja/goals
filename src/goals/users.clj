(ns goals.users
  (:require
     [next.jdbc :as jdbc]))

(defn save [{:keys [username password]} ds]
  (let [id  (random-uuid)]
  (jdbc/execute-one! ds ["INSERT INTO users(id, username, password)
  values(?, ?, ?)" id username password])))

(defn add [{{:keys [body]} :parameters ds :ds}] 
  (try
    (let [user body]
      (save user ds);; takes goal
      {:status  200})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))