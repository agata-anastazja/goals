(ns goals.core
    (:require
     [next.jdbc :as jdbc]
     [next.jdbc.date-time]))

 
(java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC")) 
(defn now [] (new java.util.Date))
;; clojure.java.time

(def df (java.text.SimpleDateFormat. "yyyy-MM-dd"))

(defn save-goal [{:keys [id created-at last-updated  description level goal-parent deadline active]}
                  ds]
    (jdbc/execute-one! ds ["INSERT INTO goals(id, created_at, last_updated, goal, goal_level, goal_parent, deadline, active)
  values(?, ?, ?, ?, ?, ?, ?, ?)"
                           id created-at last-updated  description level goal-parent deadline active]))

(defn parse-goal 
  ([req] (let [{:keys [description level deadline goal-parent]} req
               id  (random-uuid)
               created-at (now)
               deadline (.parse df deadline)]
           {:id id
            :goal-parent goal-parent
            :description description
            :level level
            :deadline deadline
            :created-at created-at
            :last-updated created-at
            :active true}))
  ([req id created-at]
   (let [{:keys [description level deadline goal-parent]} req
         deadline (.parse df deadline)]
     {:id id
      :goal-parent goal-parent
      :description description
      :level level
      :deadline deadline
      :created-at created-at
      :last-updated created-at
      :active true})))

(defn add-goal [req] 
  (try
    (do
      ;; parse-goal
      (let [
            goal (parse-goal (->
                              req
                              :parameters
                              :body )) 
            ds (:ds req)]
      (save-goal goal ds)) ;; takes goal
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body   (str "Goal saved! Pew pew!")})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))
