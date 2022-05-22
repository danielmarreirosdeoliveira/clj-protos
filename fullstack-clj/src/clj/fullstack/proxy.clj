(ns fullstack.proxy
  (:require [compojure.route :as route]
            [derekchiang.ring-proxy :as proxy]))

(def handler
  (-> (route/not-found "Page not found")
      (proxy/wrap-proxy "/api" "http://0.0.0.0:3000/api")))
