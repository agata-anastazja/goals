(ns goals.core
    (:require [clojure.java.io :as io]))

(defn save-goal [])

(defn add-goal [body]
   (let [{:keys [description level]} body]
     description
    ;; save data
     "successfully saved goal") )
