(ns goals.auth
  (:require [clojure.string :as str]
            [goals.users :as users])
  (:import [java.util Base64]))

(defn decode-auth [authorisation]
  (let [decoder (Base64/getDecoder)
        string-to-check (str/replace authorisation #"Basic " "")
        decoded (->> string-to-check
                     (.decode decoder)
                     String.)
    [user password]  (str/split decoded #":")]
    [user password]))

(defn authorised? [ds authorisation]
  (if (nil? authorisation) false
      (let [[user password] (decode-auth authorisation)]
        (users/user-exists? ds user password))))