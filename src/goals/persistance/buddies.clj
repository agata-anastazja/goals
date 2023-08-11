(ns goals.persistance.buddies
  (:require
   [next.jdbc :as jdbc]))


(defn save [user-id-1 user-id-2 ds]
  (jdbc/execute-one! ds ["INSERT INTO buddies(user_id_1, user_id_2, status)
      values(?, ?, ?)" user-id-1 user-id-2 "ACTIVE"]))

(defn get-buddies [user-id ds]
  (jdbc/execute! ds ["SELECT user_id_2 FROM buddies WHERE user_id_1 = ?" user-id]))