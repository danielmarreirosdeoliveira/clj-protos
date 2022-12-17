(ns net.eighttrigrams.rocketoid.test.datastore-test
  (:require [clojure.test :as t]
            [net.eighttrigrams.rocketoid.datastore :as datastore]
            [net.eighttrigrams.rocketoid.datastore.search :as search]
            [xtdb.api :as xt]))

(defn- run-and-return []
  (let [node (xt/start-node {})]
    #_(datastore/init-node node)
    #_(datastore/insert-issue {:title "title"
                             :desc "desc"})
    (second (first nil #_(search/search-issues "")))))

(t/deftest one
 (t/testing "abc"
   (t/is 
    (= {:title "title"
        :desc "desc"} (run-and-return)))))

(defn- run-and-return-2 []
  (let [node (xt/start-node {})
        _ nil #_(datastore/init-node node)
        _ nil #_(datastore/insert-issue {:title "title1"
                                 :desc "desc"})]
    (second (first nil #_(search/search-issues "")))))

(t/deftest two
  (t/testing "two"
    (t/is
     (= {:title "title1"
         :desc "desc"} (run-and-return-2)))))