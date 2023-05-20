(ns goals.parser
  (:require
   [next.jdbc.date-time]
   [java-time.api :as jt]))

(defn now [] (jt/instant))

(defn calculate-deadline [time level]
  (condp = level
    1 (jt/plus time (jt/days 7))
    2 (jt/plus time (jt/days 31))
    3 (jt/plus time (jt/days 364))))

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