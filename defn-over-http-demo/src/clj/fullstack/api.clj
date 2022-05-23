(ns fullstack.api
  (:require [ring.util.response :refer [resource-response response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.core :as c]
            [compojure.route :as route]
            [fullstack.auth :as auth]
            [fullstack.config :as config]
            [fullstack.dispatch :as dispatch]))

(c/defroutes query-route
  (-> #(response (dispatch/handler %))
      (auth/wrap-permissions config/secret config/permissions)
      (wrap-json-response)
      (wrap-json-body {:keywords? true})))

(c/defroutes app
  (c/context "/api" []
    (c/POST "/" [] query-route))
  (route/resources "/")
  (route/not-found (fn [_req] (resource-response "public/index.html"))))
