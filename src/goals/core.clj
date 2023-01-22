(ns goals.core
    (:require 
      [next.jdbc :as jdbc]
      [clojure.java.io :as io]))

(defn save-goal [])

(defn add-goal [{{{:keys [description level]} :body} :parameters
                  ds :ds  }]
      (jdbc/execute-one! ds ["INSERT INTO goals(goal,goal_level)
  values(?, ?) " description level])
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body   (str description "! Pew pew!")})
