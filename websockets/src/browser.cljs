(ns browser
  (:require [clojure.string :as str]))

(defonce sock (atom nil))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []

  (-> js/document
      (.getElementById "b1")
      (.-onclick)
      (set! (fn [_e]
              (when @sock (.send @sock "L")))))

  (-> js/document
      (.getElementById "b2")
      (.-onclick)
      (set! (fn [_e]
              (when @sock (.send @sock "R")))))

  (js/console.log "start!!"))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init!!")
  (let [socket (js/WebSocket. "ws://localhost:1234")]
    (set! (.-onopen socket) (fn [_e] #__))
    (set! (.-onmessage socket) (fn [_e]
                                 (-> js/document
                                     (.getElementById (if (str/starts-with? (.-data _e) "1_") "h1" "h2"))
                                     (.-innerHTML)
                                     (set! (get (str/split (.-data _e) "_") 1)))))
                                 
    (reset! sock socket)
  (start)))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
