(ns goals.goals
    (:require
     [goals.parser :as parser]
     [goals.persistance.goals :as persistance]
     [goals.persistance.users :as user-persistance]
     [clojure.data.json :as json]
     [goals.auth :as auth]))

(defn valid-goal? [goal ds]
  (or (nil? (:goal-parent goal))
   (let [goal-parent-id (:goal-parent goal)]
    (persistance/get-goal-from-db goal-parent-id ds))))


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

(defn get-all-goals [req]
  (try
    (let [ds (:ds req)
          level (->
                 req
                 :parameters
                 :body
                 :level)
          goals (persistance/get-all-goals ds level)]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  goals})
    (catch Exception e {:status 500
                        :headers {"Content-Type" "text/html"}
                        :body (str  "caught exception: " (.getMessage e))})))

(defn get-all-goals-with-their-parent[req]
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

(defn add [req]
  (try
    (let [goal (parser/parse (->
                              req
                              :parameters
                              :body))
          {{:strs [authorization]} :headers} req
          [username password] (auth/decode-auth authorization)
          ds (:ds req)
          user (->> 
                (user-persistance/get-user ds username password))
          user-id (:id user)
          goal-with-user-id (assoc goal :user-id user-id)]
       (if (valid-goal? goal ds)

         (do (persistance/save-goal-with-user goal-with-user-id ds)
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


(defn get-with-user[req]
  (try
    (let [level (->
                 req
                 :parameters
                 :body
                 :level)
          {{:strs [authorization]} :headers} req
          [username password] (auth/decode-auth authorization)
          ds (:ds req)
          user (->>
                (user-persistance/get-user ds username password))
          user-id (:id user)
          goals (persistance/get-all-goals-with-user ds user-id level)] 
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  goals})
    (catch Exception e
      (let [message (.getMessage e)]
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (str  "caught exception: " message)}))))