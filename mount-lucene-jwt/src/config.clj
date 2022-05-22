(ns config
  (:require
   [cprop.core :refer [load-config]]
   [env :refer [cprops-path]]))

(def config (load-config :file cprops-path))