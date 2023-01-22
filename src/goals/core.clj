(ns goals.core
    (:require [clojure.java.io :as io]))

(defn save-goal [])

(defn add-goal [{{{:keys [description level]} :body} :parameters}]
     {:status  200
      :headers {"Content-Type" "text/html"}
      :body   (str description "! Pew pew!")})
