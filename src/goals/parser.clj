(ns goals.parser
  (:require [java-time.api :as jt]))

(java.util.TimeZone/setDefault (java.util.TimeZone/getTimeZone "UTC"))

(defn now [] (jt/zoned-date-time))

(defn calculate-deadline [time level]
  (jt/plus time (jt/days 7)))

(defn parse
  ([req] (let [id  (random-uuid)
               created-at (now)]
           (parse req id created-at)))
  ([req id created-at]
   (let [{:keys [description level goal-parent]} req
         deadline (calculate-deadline created-at level)]
     {:id id
      :goal-parent goal-parent
      :description description
      :level level
      :deadline deadline
      :created-at created-at
      :last-updated created-at
      :active true})))