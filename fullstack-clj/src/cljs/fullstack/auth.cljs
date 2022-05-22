(ns fullstack.auth
  (:require [reagent.core :as reagent :refer [atom]]
            [accountant.core :as accountant]))

(defonce token-atom (atom ""))

(defn login-handler [on-failure]
  (fn [token]
    (if (= token "")
      (on-failure)
      (do
        (reset! token-atom token)
        (accountant/navigate! "/")))))

(defn log-out []
  (reset! token-atom ""))
