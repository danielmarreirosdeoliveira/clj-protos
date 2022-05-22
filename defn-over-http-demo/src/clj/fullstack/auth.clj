(ns fullstack.auth
  (:require [clojure.string :as str]
            [ring.util.response :refer [response]]
            [buddy.sign.jwt :as jwt]))

(defn make-login-handler [secret users]
  (fn [request]
    (let [body (:body request)
          name (:name body)
          pass (:pass body)]
      (response  (if (and (not (nil? name))
                          (not (nil? pass))
                          (not (nil? (get users name)))
                          (= (get users name) pass))
                   (jwt/sign {:user name} secret)
                   "")))))

(defn wrap-permissions [handler secret permissions]
  (fn [request]
    (let [user (try
                 (let [auth-header (get (:headers request) "authorization")]
                   (:user (jwt/unsign (str/replace auth-header "Bearer " "") secret)))
                 (catch Exception _e "anonymous"))
          request (assoc-in request [:body :server-args :permissions] (get permissions user))]
      (handler request))))
