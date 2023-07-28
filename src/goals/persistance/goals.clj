(ns goals.persistance.goals
  (:require 
             [next.jdbc :as jdbc]
[next.jdbc.result-set :as rs]))



(defn save-goal [{:keys [id created-at last-updated  description level goal-parent deadline active user-id]}
                           ds]
  (jdbc/execute-one! ds ["INSERT INTO goals(id, user_id, created_at, last_updated, goal, goal_level, goal_parent, deadline, active)
  values(?, ?, ?, ?, ?, ?, ?, ?, ?)"
                         id user-id created-at last-updated  description level goal-parent deadline active]))

(defn get-goal-from-db [id ds]
  (-> (jdbc/execute! ds ["SELECT * FROM goals where id=?" id] {:builder-fn rs/as-unqualified-lower-maps})
      first))

(defn completion-update [id current-time ds]
  (jdbc/execute-one! ds ["UPDATE goals SET active=false, last_updated= ?, date_completed= ?  WHERE id = ?"
                         current-time current-time id]))

(defn get-all-goals-with-user [ds user-id level]
  (jdbc/execute! ds ["SELECT * FROM goals where goal_level=?::TEXT AND user_id=?" level user-id] {:builder-fn rs/as-unqualified-lower-maps}))