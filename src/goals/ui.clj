(ns goals.ui
  (:require [hiccup.core :as markup]))


(defn welcome [req]
  (prn "here")
  #_{:clj-kondo/ignore [:deprecated-var]} 
  #_(markup/html [:span {:class "foo"} "bar"])
  "Hello World!")