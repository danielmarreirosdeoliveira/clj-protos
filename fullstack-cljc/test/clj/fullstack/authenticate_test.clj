(ns fullstack.authenticate-test
  (:require [fullstack.server-helper :refer [start-server! addr]]
            [clojure.test :as t :refer [deftest is]]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(defn start-server [f]
  (start-server!)
  (f))

(t/use-fixtures :once start-server)

(defn authenticate [pass]
  (let [body (json/encode {:name "user-1" :pass pass})
        options {:body body :content-type :json}
        response (http/post (str addr "/api/login") options)]
    (:body response)))

(deftest login-successfully
  (let [token-length (count (authenticate "pass-1"))]
    (is (> token-length 40))))

(deftest login-failed
  (let [token-length (count (authenticate "pass-"))]
    (is (= token-length 0))))
