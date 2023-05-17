(ns goals.goals
    (:require
     [goals.parser :as parser]
     [next.jdbc :as jdbc]
     [next.jdbc.date-time]
     [clojure.data.json :as json]))

(defn save-goal [{:keys [id created-at last-updated  description level goal-parent deadline active]}
                 ds]
  (jdbc/execute-one! ds ["INSERT INTO goals(id, created_at, last_updated, goal, goal_level, goal_parent, deadline, active)
  values(?, ?, ?, ?, ?, ?, ?, ?)"
                         id created-at last-updated  description level goal-parent deadline active]))
 


(defn add [req] 
  (try
    (let [goal (parser/parse (->
                            req
                            :parameters
                            :body))
          ds (:ds req)]
      (save-goal goal ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:id (:id goal)})})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))

(defn completion-update [id current-time ds]
  (jdbc/execute-one! ds ["UPDATE goals SET active=false, last_updated= ?, date_completed= ?  WHERE id = ?"
                          current-time current-time id]))

(defn complete [req]
  (try
    (let [id  (->
               req
               :parameters
               :body
               :id)
          ds (:ds req)
          current-time (parser/now)]
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