(ns goals.ui
  (:require [hiccup.core :as markup]
            [goals.users :as users]
            [clojure.data.json :as json]))



(defn subscribe []
  [:div {:class "well"}
   [:div "text"]
   [:form {:novalidate "" :role "form" :method "post" :action "/users"}
    [:label {:for "username"} "username"]
      ;;  <input type="text" name="name" id="name" required />
    [:input {:type "text" :name "username" :id "username"}]
    [:br]
    [:label {:for "password"} "password"]
    [:input {:type "text" :name "password" :id "password"}]
      ;;   <input type="submit" value="Subscribe!" />

    [:input {:type "submit" :value "Register"}]] ])

(defn index []

  #_{:clj-kondo/ignore [:deprecated-var]}
  (markup/html [:span {:class "foo"} (subscribe)]))


(defn welcome [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  (index)})


(defn post-sign-up [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  "thank you for signing up! tut!"})