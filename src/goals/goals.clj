(ns goals.goals
    (:require
     [goals.parser :as parser]
     [next.jdbc :as jdbc]
     [next.jdbc.result-set :as rs] 
     [clojure.data.json :as json]
     [goals.migrate :as migrate]))

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

(defn tap [x]
  (do
   
   (println "tap " x)
   x))

(defn get-goal-from-db [id ds]
  (-> (jdbc/execute! ds ["SELECT * FROM goals where id=?"id] {:builder-fn rs/as-unqualified-lower-maps})
      first))

(defn get-goal [req]
   (try
     (let [{{:keys [id]} :path-params} req
           ds (:ds req)
           goal (get-goal-from-db id ds)]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body  goal})
     (catch Exception e (do
                          (prn (str "caught exception: " (.getMessage e)))
                          {:status 500
                           :headers {"Content-Type" "text/html"}
                           :body (str "Goal not found! Loose yourself!")}))))

(comment
  (def uri "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals")
  (def ds (jdbc/get-datasource {:jdbcUrl uri}))
  (def    req {:parameters
               {:body {:description "have fun this week"
                       :level 1}}
               :ds ds})
  (migrate/migrate uri)
  (def one (add req)) 
  (def uuid (parse-uuid "2822fbca-04a6-4730-9c71-25a68343219c"))
  (def goal (get-goal-from-db uuid ds))
  goal
  (:goal goal)
  (first goal) 

  (.typeOf goal)
  )