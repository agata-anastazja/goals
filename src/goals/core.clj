(ns goals.core)

(defn add-goal [goal]
    {:status  200
    :headers {"Content-Type" "text/html"}
    :body    goal})
