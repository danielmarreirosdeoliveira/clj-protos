(ns fullstack.core-test
    (:require
     [cljs.test :as t]
     [fullstack.utils :refer [multiply]]))

(t/deftest multiply-test
  (t/is (= (* 1 2) (multiply 1 2))))

(t/deftest multiply-test-2
  (t/is (= (* 75 10) (multiply 10 75))))
