(ns go
  (:require [org.httpkit.server :as s]
            [hiccup.core :as h]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn handler [request]
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (s/with-channel request channel
    (let [_a (get (:headers request) "sec-websocket-key")]
      (s/on-close channel (fn [status] (println "channel closed: " status)))
      (s/on-receive channel (fn [data]
                              (if (= data "L")
                                (s/send! channel (str "1_" (h/html [:span "AAA"])))
                                (s/send! channel (str "2_" (h/html [:span "BBB"])))))))))

(defn -main [& _args]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and https://http-kit.github.io/migration.html#reload
  (reset! server (s/run-server #'handler {:port 1234})))