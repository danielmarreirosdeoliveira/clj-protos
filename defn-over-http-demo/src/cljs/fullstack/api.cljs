(ns fullstack.api
  (:require [fullstack.auth :as auth])
  (:require-macros [net.eighttrigrams.defn-over-http.core :refer [defn-over-http]]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def api-path "/api")

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def base-error-handler #(prn "error caught by base error handler:" %))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn fetch-base-headers []
  (let [token @auth/token-atom]
    (if (= "" token)
      {}
      {"Authorization" (str "Bearer " token)})))

(defn-over-http list-resources [])