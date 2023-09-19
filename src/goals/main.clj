(ns goals.main
  (:gen-class)
  (:require 
    [goals.handler :as handler]
    [goals.migrate :as migrate]
    [ring.adapter.jetty :as jetty]
    [next.jdbc :as jdbc]
    [clojure.tools.logging :as log]))

(def app
  (let [connection-url (or (System/getenv "DB_JDBC_URI") 
                           "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals")]
    (handler/server (jdbc/get-datasource {:jdbcUrl connection-url}))))
  

(defn set-up-db []
   (let [connection-url (or (System/getenv "DB_JDBC_URI")
                           "jdbc:postgresql://127.0.0.1:5432/goals?user=goals&password=goals")]
    (migrate/migrate connection-url)))

(defn -main
  [& args]
  (set-up-db)
  (log/info "starting server")
  (jetty/run-jetty #'app
                   {:host   "0.0.0.0"
                    :port   8080
                    :join?  false
                    :async? true}))



(comment
      (set-up-db)

  (def server (jetty/run-jetty #'app {:host   "0.0.0.0"
                                           :port   8080
                                           :join?  false
                                           :async? true}))
  #_(.stop server)
  )