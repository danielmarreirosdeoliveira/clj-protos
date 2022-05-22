(ns editor-test
  (:require [cljs.test :refer (deftest is)]
            [editor :as e]))

(defn init [& _args])

(deftest base-case
  (is (= "hello, dan" (e/hello "dan"))))
