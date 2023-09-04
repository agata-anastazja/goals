(ns goals.ui
  (:require [hiccup.core :as markup]
            [goals.auth :as auth]))


(defn logged-in? [headers]
  (tap> headers)
  (if (:authorization headers)
    (let [[username _] (auth/decode-auth (:authorization headers))]
      (str "Welcome " username "!")) "Please log in!"))

(defn subscribe [req]
  [:div {:class "well"}
   [:div "Register"
    [:form {:novalidate "" :role "form" :method "post" :action "/users"}
     [:label {:for "username"} "username"]
     [:input {:type "text" :name "username" :id "username"}]
     [:br]
     [:label {:for "password"} "password"]
     [:input {:type "text" :name "password" :id "password"}]
     [:input {:type "submit" :value "Register"}]
     [:br]]]
   [:div "Log in"
    [:form {:novalidate "" :role "form" :method "post" :action "/log-in"}
     [:label {:for "username"} "username"]
     [:input {:type "text" :name "username" :id "username"}]
     [:br]
     [:label {:for "password"} "password"]
     [:input {:type "text" :name "password" :id "password"}]
     [:input {:type "submit" :value "Log in"}]
     [:br]]]
   [:div (logged-in? (:headers req))]])

(defn index [req]

  #_{:clj-kondo/ignore [:deprecated-var]}
  (markup/html [:span {:class "foo"} (subscribe req)]))


(defn welcome [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
 
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body  (index req)})


(defn post-sign-up [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  "thank you for signing up! tut!"})