(ns goals.goals
  (:require
   [clojure.tools.logging :as log]
   [goals.parser :as parser]
   [goals.persistance.goals :as persistance]
   [goals.persistance.users :as user-persistance]
   [clojure.data.json :as json]
   [goals.auth :as auth]))

(defn valid-goal? [goal ds]
  (or (nil? (:goal-parent goal))
   (let [goal-parent-id (:goal-parent goal)]
    (persistance/get-goal-from-db goal-parent-id ds))))

(defn get-user-id [req]
  (let [{{:strs [authorization]} :headers} req
        [username password] (auth/decode-auth authorization)
        ds (:ds req)
        user (->>
              (user-persistance/get-user ds username password))]
    (:id user)))

(defn complete [req]
  (try
    (let [id  (->
               req
               :parameters
               :body
               :id)
          ds (:ds req)
          current-time (parser/now)]
      (persistance/completion-update id current-time ds)
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  (json/write-str {:id id})})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not completed! Complete yourself!")}))))

(defn get-goal [req]
   (try
     (let [{{:keys [id]} :path-params} req
           ds (:ds req)
           goal (persistance/get-goal-from-db id ds)]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  goal})
     (catch Exception e (do
                          (prn (str "caught exception: " (.getMessage e)))
                          {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (str "Goal not found! Loose yourself!")}))))


(defn add [req]
  (try
    (let [goal (parser/parse (->
                              req
                              :parameters
                              :body))
          ds (:ds req)
          user-id (get-user-id req)
          goal-with-user-id (assoc goal :user-id user-id)]
       (if (valid-goal? goal ds)
         (do
           (persistance/save-goal goal-with-user-id ds)
           {:status  200
            :headers {"Content-Type" "application/json"}
            :body  (json/write-str {:id (:id goal)})})
         {:status 400
          :headers {"Content-Type" "text/html"}
          :body (str "Goal not saved! Save yourself! Invalid request")}))
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))


(defn get-all-goals[req]
  (log/trace "Got to inside of get-all-goals")
  (try
    (let [level (->
                 req
                 :parameters
                 :body
                 :level)
          user-id (get-user-id req)
          ds (:ds req)
          goals (persistance/get-all-goals-with-user ds user-id level)] 
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  "foo"})
    (catch Exception e
      (log/error e "Unexpected exception thrown when trying to get all goals")
      #_(let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))


(defn get-all-goals-with-their-parent [req]
  (try
    (let [goals (-> (get-all-goals req) :body)
          ds (:ds req)
          goals-with-parent (mapv (fn [goal] (assoc goal :goal-parent (persistance/get-goal-from-db (:goal_parent goal) ds))) goals)]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  goals-with-parent})
    (catch Exception e
      {:status 500
       :headers {"Content-Type" "text/html"}
       :body (str  "caught exception: " (.getMessage e))})))
