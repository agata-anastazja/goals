(ns goals.ui
  (:require [hiccup.core :as markup]
            [clojure.data.json :as json]))



(defn subscribe []
  [:div {:class "well"}
   [:div "text"]
   [:form {:novalidate "" :role "form" :method "post"}
    [:label {:for "username"} "username"]
      ;;  <input type="text" name="name" id="name" required />
    [:input {:type "text" :name "username" :id "username"}]
      ;;   <input type="submit" value="Subscribe!" />
    [:input {:type "submit" :value "Register"}]]
  
    ;; [:div {:class "form-group"}
    ;; ;;  <label for="name">Enter your name: </label>
    ;;  (markup/label {:class "control-label"} "email" "Email")
    ;;  (email-field {:class "form-control" :placeholder "Email" :ng-model "user.email"} "user.email")]
    ;; [:div {:class "form-group"}
    ;;  (label {:class "control-label"} "password" "Password")
    ;;  (password-field {:class "form-control" :placeholder "Password" :ng-model "user.password"} "user.password")]
    ;; [:div {:class "form-group"}
    ;;  (label {:class "control-label"} "gender" "Gender")
    ;;  (reduce conj [:div {:class "btn-group"}] (map labeled-radio ["male" "female" "other"]))]
    ;; [:div {:class "form-group"}
    ;;  [:label
    ;;   (check-box {:ng-model "user.remember"} "user.remember-me") " Remember me"]]
   
  ;;  [:pre "form = {{ user | json }}"]
   ])

(defn index[]
  
  #_{:clj-kondo/ignore [:deprecated-var]}
  (markup/html [:span {:class "foo"} (subscribe)]))


(defn welcome [req]
  #_{:clj-kondo/ignore [:deprecated-var]}
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body  (index)})