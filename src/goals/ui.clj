(ns goals.ui
  (:require [hiccup.core]
            [clojure.data.json :as json]))

(defn welcome [req]
  (prn "here")
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body  (json/write-str {:buddies "welcome Agata"})})