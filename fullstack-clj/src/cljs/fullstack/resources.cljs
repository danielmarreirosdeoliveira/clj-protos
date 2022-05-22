(ns fullstack.resources
  (:require [reagent.core :as reagent :refer [atom]]
            ;; [tsfun :as ts-fu] ;; just to demo bundling of npm packages
            [fullstack.auth :as auth]
            [fullstack.api :as api]))

(defn- search-component [q on-change]
  [:div.search
   [:input
    {:type "text"
     :value @q
     :placeholder "search-term"
     :on-change (fn [change] (on-change (aget change "target" "value")))}]])

(defn- list-component [result]
  (fn []
    [:ul
     (doall (map-indexed (fn [index item] [:li {:key index} (:name item)])
                         @result))]))

(defn component []  
  ;; (js/console.log (ts-fu/map #(* 2 %) (clj->js [1 2 3]))) ;; see requires
  (let [q      (atom "")
        result (atom [])
        handler #(reset! result %)
        on-change (fn [new-q] 
                    (reset! q new-q)
                    (api/get-resources @q @auth/token-atom handler))]
    (fn []
      (api/get-resources @q @auth/token-atom handler)
      [:div
       [search-component q on-change]
       [list-component result]])))
