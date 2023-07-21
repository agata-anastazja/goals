(ns goals.persistance.buddy-requests
  (:require
   [next.jdbc :as jdbc]))

(defn save [requestee-id requester-id ds]
  (jdbc/execute-one! ds ["INSERT INTO buddy_requests(requestee_id, requester_id, status)
      values(?, ?, ?)" requestee-id requester-id "PENDING"]))

(defn get-received-requests[requestee-id ds]
  (jdbc/execute! ds ["SELECT requester_id, status FROM buddy_requests WHERE requestee_id = ?" requestee-id]))