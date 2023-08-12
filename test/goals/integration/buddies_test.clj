(ns goals.integration.buddies-test
  (:require
   [goals.users :as users]
   [goals.buddies :as buddies]
   [clojure.test :refer :all]
   [clojure.data.json :as json]
   [goals.integration.test-utils :as test-utils]
   [goals.buddy-requests :as buddy-requests]))

(deftest add-buddy-request-test
  (testing "accepting a request adds a buddy"
    (with-open [conn (test-utils/create-connection)]
      (let [user {:username "RahulUnique"
                  :password "secretsecret"}
            user-req {:parameters {:body user}
                      :ds conn}
            requester (test-utils/ensure-user user-req)
            requester-id (-> (json/read-json (:body requester)) :id)

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
            buddy-req-accepted (buddy-requests/accept {:parameters {:body {:user-id-1 requester-id
                                                                           :user-id-2 requestee-id
                                                                           :buddy-request-id (-> (json/read-json (:body received-buddy-requests)) :buddy-requests first :id)}}
                                                       :ds conn
                                                       :headers {"authorization" requestee-auth-header}})
            _ (assert (= 200 (:status buddy-req-accepted)))
            result1 (buddies/get-buddies {:ds conn
                                          :headers {"authorization" auth-header}})
            result2 (buddies/get-buddies {:ds conn
                                          :headers {"authorization" requestee-auth-header}})]
        (is (= 200 (:status result1)))
        (is (= [(str requestee-id)] (-> (:body result1)
                        json/read-json
                        :buddies)))
        (is (= 200 (:status result2)))
        (is (= [(str requester-id)] (-> (:body result2)
                                       json/read-json
                                       :buddies)))))))