(ns goals.goals
    (:require
     [goals.parser :as parser]
     [goals.persistance :as persistance]
     [clojure.data.json :as json]))


(defn valid-goal? [goal ds]
  (or (nil? (:goal-parent goal))
   (let [goal-parent-id (:goal-parent goal)]
    (persistance/get-goal-from-db goal-parent-id ds))))

(defn add [req] 
  (println req)
  (try
    (let [goal (parser/parse (->
                            req
                            :parameters
                            :body))
          ds (:ds req)]
      (if (valid-goal? goal ds)
        (do
          (persistance/save-goal goal ds)
          {:status  200
           :headers {"Content-Type" "application/json"}
           :body  (json/write-str {:id (:id goal)})})
        {:status 400
         :headers {"Content-Type" "text/html"}
         :body (str "Goal not saved! Save yourself! Invalid request")}))

    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))


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
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goals not found! Loose yourself!")}))))