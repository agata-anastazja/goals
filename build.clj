(ns build
  (:refer-clojure :exclude [test])
  (:require [org.corfield.build :as bb]))

(def lib 'net.clojars.goals/api)
(def version "0.1.0-SNAPSHOT")
(def main 'goals.main)

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci " build the uberjar" [opts]
  (-> opts
      (assoc :lib lib :version version :main main)
      (bb/clean)
      (bb/uber)))
