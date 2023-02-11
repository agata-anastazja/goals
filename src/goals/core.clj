(ns goals.core
    (:require
     [next.jdbc :as jdbc]
     [next.jdbc.date-time]
     [next.jdbc.result-set :as rs])
  (:import (java.util UUID)
           (java.sql Array)))

(extend-protocol rs/ReadableColumn
   Array
   (read-column-by-label [^Array v _]    (vec (.getArray v)))
   (read-column-by-index [^Array v _ _]  (vec (.getArray v)))) 
 
(java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC")) 
(defn now [] (new java.util.Date))
;; clojure.java.time
(def df (java.text.SimpleDateFormat. "yyyy-MM-dd"))
 
(defn save-goal [{{{:keys [description level deadline goal-parents]} :body} :parameters
                  ds :ds}]

  (let [id  (UUID/randomUUID)
        created-at (now)
        last-updated created-at
        deadline (.parse df deadline)
        goal-parents (into-array String goal-parents)
        active true]

    (jdbc/execute-one! ds ["INSERT INTO goals(id, created_at, last_updated, goal, goal_level, goal_parents, deadline, active)
  values(?, ?, ?, ?, ?, ?, ?, ?)"
                           id created-at last-updated  description level goal-parents deadline active])))


(defn add-goal [req] 
  (try
    (do
      ;; parse-goal
      (save-goal req) ;; takes goal
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body   (str "Goal saved! Pew pew!")})
    (catch Exception e (do
                         (prn (str "caught exception: " (.getMessage e)))
                         {:status 500
                          :headers {"Content-Type" "text/html"}
                          :body (str "Goal not saved! Save yourself!")}))))
