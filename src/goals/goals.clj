(ns goals.goals
    (:require
     [next.jdbc :as jdbc]
     [next.jdbc.date-time]
     [clojure.data.json :as json]))

 
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
  ([req] (let [id  (random-uuid)
               created-at (now)]
          (parse-goal req id created-at)))
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

(defn add [req] 
  (try
    (let [goal (parse-goal (->
                            req
                            :parameters
                            :body))
          ds (:ds req)]
      (save-goal goal ds);; takes goal
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:id (:id goal)})})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))

(defn completion-update [id current-time ds]
  (jdbc/execute-one! ds ["UPDATE goals SET active=false WHERE id = ?"
                         id]))

(defn complete [req]
  (try
    (let [id  (->
               req
               :parameters
               :body
               :id)
          ds (:ds req)
          current-time (now)]
      (completion-update id current-time ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:id id})})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not completed! Complete yourself!")}))))


(defn get-goal [{{:keys [id]} :path-params}]
   (try
       {:status  200
        :headers {"Content-Type" "application/json"}
        :body  {:id id}}
     (catch Exception e (do
                          (prn (str "caught exception: " (.getMessage e)))
                          {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (str "Goal not found! Loose yourself!")}))))