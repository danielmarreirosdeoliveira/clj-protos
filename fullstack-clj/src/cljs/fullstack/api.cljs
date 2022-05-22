(ns fullstack.api
  (:require [ajax.core :refer [POST json-request-format]]))

(defn get-resources [q token handler]
  (let [request {:response-format :json
                 :keywords? true
                 :headers {"Content-Type" "application/json"}
                 :format (json-request-format)
                 :body (.stringify js/JSON (clj->js {:query-type "resources" :query {:q q}}))
                 :handler #(handler (:result %))}
        request (if (= "" token)
                  request
                  (assoc-in request [:headers "Authorization"] (str "Bearer " token)) )]
    (POST "/api" request)))