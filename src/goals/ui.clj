(ns goals.ui
  (:require [hiccup.core :as markup]
            [clojure.data.json :as json]))



(defn welcome [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  (markup/html [:span {:class "foo"} "Monkeys are adorable!"])})