(ns goals.persistance.users
   (:require
    [next.jdbc :as jdbc]))

(defn save [{:keys [username password]} ds]
  (let [id (random-uuid)]
    (jdbc/execute-one! ds ["INSERT INTO users(id, username, password)
      values(?, ?, ?)" id username password])))

(defn get-user [ds username password]
  (jdbc/execute-one! ds ["SELECT * FROM users WHERE username = ? AND password = ?" username password]))
