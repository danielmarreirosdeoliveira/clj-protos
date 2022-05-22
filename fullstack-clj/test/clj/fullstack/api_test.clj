(ns fullstack.api-test
  (:require [clojure.test :as t :refer [deftest is]]
            [fullstack.server-helper :refer [start-server! addr]]
            [fullstack.authenticate-test :as authenticate-test]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(defonce api-addr (str addr "/api"))

(defonce token (atom ""))

(defn authenticate [f]
  (start-server!)
  (reset! token (authenticate-test/authenticate "pass-1"))
  (f))

(t/use-fixtures :once authenticate)

(defn- post-request [token query-type query]
  (let [body     (json/encode {:query-type query-type :query query})
        options  {:content-type :json
                  :body         body}
        options (if (nil? token) options (assoc options :headers {"authorization" (str  "Bearer " token)}))
        response (http/post api-addr options)]
    [(:status response) (json/decode (:body response) true)]))


(defn- get-permissions [token]
  (update (post-request token "permissions" {}) 1 :permissions))

(deftest authorize-properly
  (let [[status permissions] (get-permissions @token)]
    (is (= 200 status))
    (is (= "all" permissions))))

(deftest bad-token
  (let [[status permissions] (get-permissions "abc")]
    (is (= 200 status))
    (is (= "none" permissions))))


(defn- get-resources [q token]
  (:result (second (post-request token "resources" {:q q}))))

(deftest resources-for-admin
  (is (= ["one" "two" "three"]
         (map :name (get-resources "" @token)))))

(deftest filtered-resources-for-admin
  (is (= ["two" "three"]
         (map :name (get-resources "t" @token)))))

(deftest resources-for-anonymous
  (is (= ["one" "two"]
         (map :name (get-resources "" nil)))))

(deftest filtered-resources-for-anonymous
  (is (= ["two"]
         (map :name (get-resources "t" nil)))))


(deftest route-not-found
  (let [error-msg (try (http/post (str api-addr "/1") {:accept :json :headers {"authorization" (str  "Bearer " @token)}})
                       (catch Exception e (.getMessage e)))]
    (is (= "clj-http: status 404"
             error-msg))))
