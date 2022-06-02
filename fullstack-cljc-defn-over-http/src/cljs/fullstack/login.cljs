(ns fullstack.login
  (:require [reagent.core :as reagent :refer [atom]]
            [fullstack.auth :as auth]
            [fullstack.api :as api]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn- show-msg [msg-shown]
  (fn []
    (reset! msg-shown true)
    (js/setTimeout (fn [] (reset! msg-shown false)) 1500)))

(defn- fetch-token [msg-shown login-name login-pass]
  (go ((auth/login-handler (show-msg msg-shown)) 
       (-> {:name @login-name :pass @login-pass}
           #_{:clj-kondo/ignore [:unresolved-var]}
           api/log-in
           <p!))))

(defn- input-field [property-a placeholder left]
  [:input
   {:type "text"
    :style {:left left :position :absolute}
    :value @property-a
    :placeholder placeholder
    :on-change (fn [change] (reset! property-a (aget change "target" "value")))}])

(defn- form [login-name login-pass]
  [:<>
   [:div
    {:style {:height "40px"}}
    "Name: "
    [input-field login-name "name" "60px"]]
   [:div
    {:style {:height "40px"}}
    "Pass: "
    [input-field login-pass "pass" "60px"]]])

(defn component []
  (let [msg-shown (atom false)
        [login-name login-pass] [(atom "user-1") (atom "pass-1")] ;; for demo purposes
        ]
    (fn []
      [:div.login
       {:style {:position :relative}}
       [form login-name login-pass]
       [:button {:on-click (fn [_] (fetch-token msg-shown login-name login-pass))} "Log in!"]
       [:br]
       [:br]
       (if @msg-shown [:div "Login attempt was unsuccessful"] [:<>])])))
