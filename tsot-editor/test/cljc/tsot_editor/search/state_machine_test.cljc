(ns tsot-editor.search.state-machine-test
  (:require [clojure.test :refer [deftest is]]
            [tsot-editor.search.state-machine :as state-machine]))

(deftest test-set-input:string
  (let [[get-state dispatch!] (state-machine/create (fn [_] ["a" "b" "c"])
                                                    #(prn "hi")
                                                    atom)]

    (dispatch! {:type         :set-input:string
                :input:string "abc"})
    (is (= "abc" (:input:string (get-state))))
    (is (= ["a" "b" "c"] (:suggestions (get-state))))))

(deftest test-adjust-suggestions-if-necessary
  (let [[get-state dispatch!] (state-machine/create
                               (let [a (atom 0)]
                                 (fn [_]
                                   (if (= 0 @a)
                                     (do (swap! a inc) ["a" "b" "c"])
                                     ["a" "c"])))
                               #(prn "hi")
                               atom)]
    (dispatch! {:type :make-suggestions})
    (dispatch! {:type :select-below})
    (dispatch! {:type         :set-input:string
                :input:string "some-value"})
    (is (= 0 (:selected-suggestion:id (get-state))))))

(deftest test-adjust-suggestions-not-necessary
  (let [[get-state dispatch!] (state-machine/create
                               (let [a (atom 0)]
                                 (fn [_]
                                   (if (= 0 @a)
                                     (do (swap! a inc) ["a" "b" "c"])
                                     ["a" "b"])))
                               #(prn "hi")
                               atom)]
    (dispatch! {:type :make-suggestions})
    (dispatch! {:type :select-below})
    (dispatch! {:type         :set-input:string
                :input:string "some-value"})
    (is (= 1 (:selected-suggestion:id (get-state))))))

(deftest shorten
  (let [[get-state dispatch!] (state-machine/create (fn [_] [])
                                                    #(prn "hi")
                                                    atom)]
    (dispatch! {:type         :set-input:string
                :input:string ":r "})
    (dispatch! {:type         :set-input:string
                :input:string ":r"})
    (is (= "" (:input:string (get-state))))))

(deftest dont-shorten
  (let [[get-state dispatch!] (state-machine/create (fn [_] [])
                                                    #(prn "hi")
                                                    atom)]
    (dispatch! {:type         :set-input:string
                :input:string ":"})
    (dispatch! {:type         :set-input:string
                :input:string ":r"})
    (is (= ":r" (:input:string (get-state))))))

(deftest suggest-create
  (let [[get-state dispatch!] (state-machine/create (fn [_] [])
                                                    #(prn "hi")
                                                    atom)]
    (dispatch! {:type         :set-input:string
                :input:string "abc"})
    (is (= ":n abc" (:input:string (get-state))))))
