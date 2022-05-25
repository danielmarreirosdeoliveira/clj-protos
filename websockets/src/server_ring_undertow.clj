(ns server-ring-undertow
  (:require [clojure.java.io :as io]
            [ring.adapter.undertow :refer [run-undertow]]
            [ring.adapter.undertow.websocket :as ws]
            [hiccup.core :as h]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn ws-handler [_request]
  {:undertow/websocket
   {:on-open (fn [{:keys [_channel]}] (println "WS open!"))
    :on-message (fn [{:keys [channel data]}] 
                  (if (= data "L")
                                (ws/send (str "1_" (h/html [:span "AAA"])) channel)
                                (ws/send (str "2_" (h/html [:span "BBB"])) channel)))
    :on-close   (fn [{:keys [_channel _ws-channel]}] (println "WS closed!"))}})

(defn resource-handler [req]
  {:status 200
   :body (slurp (io/resource
                 (if (= "/" (:uri req))
                   "index.html"
                   (subs (:uri req) 1))))})

(defn app [req]
  (if (= "/ws" (:uri req))
    (ws-handler req)
    (resource-handler req)))

(defn -main [& _args]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and https://http-kit.github.io/migration.html#reload
  (reset! server (run-undertow #'app {:port 4000})))