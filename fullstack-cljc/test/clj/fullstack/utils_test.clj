(ns fullstack.utils-test
  (:require [clojure.test :as t]
            [fullstack.utils :refer [multiply]]))

(t/deftest test-adder
  (t/is (= 1 (multiply 1 1))))
