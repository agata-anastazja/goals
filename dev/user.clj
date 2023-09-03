(ns user
  (:require [portal.api :as p]))

(p/open)
(add-tap #'p/submit)
