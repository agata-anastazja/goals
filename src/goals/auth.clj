(ns goals.auth
  (:require [clojure.string :as str]
            [goals.users :as users])
  (:import [java.util Base64]))


(defn user-exists? [ds username password]
  (some? (users/get-user ds username password)))

(defn authorised? [ds authorisation]
  (let [decoder (Base64/getDecoder)
        string-to-check (str/replace authorisation #"Basic " "")
        decoded (->> string-to-check
                     (.decode decoder)
                     String.)
        [user password]  (str/split decoded #":")]
               (user-exists? ds user password)))