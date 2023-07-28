(ns goals.persistance.buddy-requests
  (:require
   [next.jdbc :as jdbc]))

(defn save [id requestee-id requester-id ds]
  (jdbc/execute-one! ds ["INSERT INTO buddy_requests(id, requestee_id, requester_id, status)
      values(?, ?, ?, ?)" id requestee-id requester-id "PENDING"]))

(defn get-received-requests[requestee-id ds]
  (jdbc/execute! ds ["SELECT id, requester_id, status FROM buddy_requests WHERE requestee_id = ?" requestee-id]))

(defn accept [buddy-request-id ds]
  (jdbc/execute-one! ds ["UPDATE buddy_requests SET status = ? WHERE id = ?" "ACCEPTED" buddy-request-id]))