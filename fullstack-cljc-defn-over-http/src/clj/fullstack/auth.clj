(ns fullstack.auth
  (:require [clojure.string :as str]
            [buddy.sign.jwt :as jwt]))

(defn login [secret users]
  (fn [_]
    (fn [{name :name pass :pass :as a}]
      (if (and (not (nil? name))
               (not (nil? pass))
               (not (nil? (get users name)))
               (= (get users name) pass))
        (jwt/sign {:user name} secret)
        ""))))

(defn wrap-permissions [handler secret permissions]
  (fn [request]
    (let [user (try
                 (let [auth-header (get (:headers request) "authorization")]
                   (:user (jwt/unsign (str/replace auth-header "Bearer " "") secret)))
                 (catch Exception _e "anonymous"))
          request (assoc-in request [:body :server-args :permissions] (get permissions user))]
      (handler request))))
