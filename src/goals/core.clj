(ns goals.core
    (:require [clojure.java.io :as io]))

(defn add-goal [request]
    (prn request)
    (with-open [r (io/reader (:body request))] (prn (slurp r)))  )
