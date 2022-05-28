(ns fullstack.api
  (:require-macros [net.eighttrigrams.defn-over-http.core :refer [defn-over-http]])
  (:require [fullstack.auth :as auth]
            ajax.core))

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

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defn-over-http list-resources [[] base-error-handler])

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defn-over-http log-in ["" base-error-handler])