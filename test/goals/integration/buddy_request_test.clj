(ns goals.integration.buddy-request-test    (:require
                                             [goals.users :as users]
                                             [clojure.test :refer :all]
                                             [clojure.data.json :as json]
                                             [goals.integration.test-utils :as test-utils]
                                             [goals.buddy-requests :as buddy-requests]))

(deftest add-buddy-request-test
  (testing "you can accept a request"
     (with-open [conn (test-utils/create-connection)]
      (let [user {:username "RahulUnique"
                  :password "secretsecret"}
            user-req {:parameters {:body user}
                      :ds conn}
            requester (test-utils/ensure-user user-req)
            requester-id (-> (json/read-json (:body requester)) :id parse-uuid)
    
            auth-header (test-utils/auth-header user)
            requestee {:username "Robert"
                       :password "Secretpassword"}
            requestee-user (users/add {:parameters
                                       {:body requestee}
                                       :ds conn})
            requestee-id (-> (json/read-json (:body requestee-user)) :id)
    
            req {:parameters {:body {:requestee-id requestee-id}}
                 :ds conn
                 :headers {"authorization" auth-header}}
            added-request (buddy-requests/add req)
    
            _ (assert (= 200 (:status added-request)) "failed to add request")
            requestee-auth-header (test-utils/auth-header requestee)
            received-buddy-requests-req {:ds conn
                                         :headers {"authorization" requestee-auth-header}}
            received-buddy-requests (buddy-requests/get-received-requests received-buddy-requests-req)
            _ (assert  (= 1 (count (-> (json/read-json (:body received-buddy-requests)) :buddy-requests))))
            _ (buddy-requests/accept {:parameters {:body {:buddy-request-id (-> (json/read-json (:body received-buddy-requests)) :buddy-requests first :id)}}
                                           :ds conn
                                           :headers {"authorization" requestee-auth-header}})
            result (buddy-requests/get-received-requests received-buddy-requests-req)]
         (is (= 200 (:status result)))
         (is (= 1 (count (-> (json/read-json (:body result)) :buddy-requests))))
          (is  (= (str requester-id) (-> (json/read-json (:body result)) :buddy-requests first :requester_id)))
         (is (= "ACCEPTED" (-> (json/read-json (:body result)) :buddy-requests first :status))))))
  
  
  (testing "you can see your added requests"
    (with-open [conn (test-utils/create-connection)]
      (let [user {:username "RahulUnique"
                  :password "secretsecret"}
            user-req {:parameters {:body user}
                      :ds conn}
            requester (test-utils/ensure-user user-req)
            requester-id (-> (json/read-json (:body requester)) :id parse-uuid)
            auth-header (test-utils/auth-header user)
            requestee {:username "Robert"
                       :password "Secretpassword"}
            requestee-user (users/add {:parameters
                                       {:body requestee}
                                       :ds conn})
            requestee-id (-> (json/read-json (:body requestee-user)) :id)
            req {:parameters {:body {:requestee-id requestee-id}}
                 :ds conn
                 :headers {"authorization" auth-header}}
            added-request (buddy-requests/add req)
            _ (assert (= 200 (:status added-request)) "failed to add request")
            requestee-auth-header (test-utils/auth-header requestee)
            received-buddy-requests-req {:ds conn
                                         :headers {"authorization" requestee-auth-header}}
            result (buddy-requests/get-received-requests received-buddy-requests-req)]
        (is (= 200 (:status result)))
        (is  (= 1 (count (-> (json/read-json (:body result)) :buddy-requests))))
        (is  (= (str requester-id) (-> (json/read-json (:body result)) :buddy-requests first :requester_id)))
        (is (= "PENDING" (-> (json/read-json (:body result)) :buddy-requests first :status)))))) )