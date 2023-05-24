(ns goals.persistance
  (:require 
             [next.jdbc :as jdbc]
[next.jdbc.result-set :as rs]))


(defn save-goal [{:keys [id created-at last-updated  description level goal-parent deadline active]}
                 ds]
  (jdbc/execute-one! ds ["INSERT INTO goals(id, created_at, last_updated, goal, goal_level, goal_parent, deadline, active)
  values(?, ?, ?, ?, ?, ?, ?, ?)"
                         id created-at last-updated  description level goal-parent deadline active]))

(defn get-goal-from-db [id ds]
  (-> (jdbc/execute! ds ["SELECT * FROM goals where id=?" id] {:builder-fn rs/as-unqualified-lower-maps})
      first))

(defn completion-update [id current-time ds]
  (jdbc/execute-one! ds ["UPDATE goals SET active=false, last_updated= ?, date_completed= ?  WHERE id = ?"
                         current-time current-time id]))

