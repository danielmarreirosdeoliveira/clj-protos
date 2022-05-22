(ns fullstack.dispatch
  (:require [ring.util.response :refer [response]]
            [fullstack.resources :as resources]))

(defn handler [request]
  (let [permissions (:permissions request)
        query-type (:query-type (:body request))
        query      (:query (:body request))]
    (cond
      (= query-type "permissions")
      (response {:permissions permissions})
      (= query-type "resources")
      (response (resources/get-resources permissions query))
      :else
      (response {:msg "no type selected"}))))
