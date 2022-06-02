(ns fullstack.api
  (:require [ring.util.response :refer [resource-response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.core :as c]
            [compojure.route :as route]
            [fullstack.auth :as auth]
            [fullstack.dispatch :as dispatch]))

(defonce secret "salkjflasj-slsajes___secret___")

(defonce users {"user-1" "pass-1"})

(defonce permissions {"user-1" "all"
                      "anonymous" "none"})

(c/defroutes login-route
  (wrap-json-body (auth/make-login-handler secret users)
   {:keywords? true}))

(c/defroutes query-route
  (-> dispatch/handler
      (auth/wrap-permissions secret permissions)
      (wrap-json-response)
      (wrap-json-body {:keywords? true})))

(c/defroutes app
  (c/context "/api" []
    (c/POST "/" [] query-route)
    (c/POST "/login" [] login-route))
  (route/resources "/")
  (route/not-found (fn [_req] (resource-response "public/index.html"))))
