(ns fullstack.server-helper
  (:require [ring.adapter.jetty :as jetty]
            [fullstack.api :as api]))

(defonce addr "http://0.0.0.0:3005")

(defonce server-started (atom false))

(defn start-server! []
  (when (not @server-started)
    (future (jetty/run-jetty api/app {:port 3005}))
    (reset! server-started true)
    (Thread/sleep 50)))
