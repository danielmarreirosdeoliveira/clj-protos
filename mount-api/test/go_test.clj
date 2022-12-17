(ns go-test
  (:require
   [clojure.test :refer :all]
   [ring.mock.request :as mock]
   [cheshire.core :as cheshire]
   [mount.core :as mount]
   go))

(def token (atom nil))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'resources/resources)
    (let [response (go/handler (-> (mock/request :post "")
                                   (mock/json-body {:function "login"
                                                    :args     {:name "dan"
                                                               :pass "key"}})))]
      (reset! token (:body response)))
    (f)))

(deftest test-app
  (testing "permissions"
    (let [response (go/handler (-> (mock/request :post "")
                                   (mock/header "Authorization" (str "Bearer " @token))
                                   (mock/json-body {:function "show-permissions"})))]
      (is (= "all" (:permissions (cheshire/parse-string (:body  response) true))))
      (is (= 200 (:status response))))))
