(ns goals.core
    (:require [clojure.java.io :as io]))

(defn add-goal [body]
   (let [{:keys [description]} body]
     description) )
