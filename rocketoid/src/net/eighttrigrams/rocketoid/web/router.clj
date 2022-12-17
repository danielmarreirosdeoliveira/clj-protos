(ns net.eighttrigrams.rocketoid.web.router
  (:require [net.eighttrigrams.rocketoid.datastore :as datastore]
            [net.eighttrigrams.rocketoid.web.app :as app]
            [net.eighttrigrams.rocketoid.web.router.commands :as commands]))

;; TODO store websockets per client; generate uuids in the frontend by which clients can identify themselves
(def ws (atom nil))

(defn ws-base-handler
  [our-ws-atom]
  (fn [_req]
    {:status 101
     :headers {"upgrade" "websocket"
               "connection" "upgrade"}
     :ws {:on-connect (fn [ws]
                        (prn (java.util.Date.) "on connect")
                        (reset! our-ws-atom ws))
          :on-text (fn [_ws _text-message]
                     #_(send-message req {:ws ws :text text-message}))
          :on-close (fn [_ws _status-code _reason]
                      (prn (java.util.Date.) "on close")
                      (reset! our-ws-atom nil))}}))

(defn key-handler [{:keys [anti-forgery-token state value net.eighttrigrams.rocketoid/ds] 
                    :as _req}]
  (let [code           (:code value)
        search-active? (:searchActive state)]
    {:status (cond (and search-active? (= "Enter" code))
                   (commands/close-active-search! ws ds state)
                   search-active?
                   (do (prn "do nothing") 200)
                   (= "KeyE" code)
                   (commands/open-modal! ws anti-forgery-token :textarea "textarea.js")
                   (= "KeyN" code)
                   (commands/open-modal! ws anti-forgery-token :input#new-issue "input.js")
                   :else 200)}))

(defn close-modal-handler [{:keys [value save? net.eighttrigrams.rocketoid/ds] 
                            :as _req}] 
  (when save?
    (datastore/insert-issue ds {:title value :description "desc"})) 
  {:status (commands/close-modal! ws ds)})

(defn active-search-handler
  [{:keys [_anti-forgery-token _state value net.eighttrigrams.rocketoid/ds]
    :as   _req}]
  {:status (commands/reload-issues! ws ds value nil)})

(defn open-active-search-handler
  [{:keys [anti-forgery-token]
    :as   _req}]
  {:status (commands/open-active-search! ws anti-forgery-token)})

(defn close-active-search-handler 
  [{:keys [_anti-forgery-token state net.eighttrigrams.rocketoid/ds]
    :as   _req}]
  {:status (commands/close-active-search! ws ds state)})

(defn- select-child-handler
  [{:keys [state net.eighttrigrams.rocketoid/ds] 
    :as _req}]
  {:status (commands/select-child! ws ds state)})

(defn- deselect-children-handler
  [{:keys [state net.eighttrigrams.rocketoid/ds]
    :as _req}]
  {:status (commands/deselect-issues! ws ds state)})

;; TODO make fewer endpoints; decide data-driven
;; TODO curious, can i make that dynamic?
(def routes
  {:routes ["/" {}
            ["" {:get app/component}]
            ["ws" {:get (ws-base-handler ws)}]
            ["keys" {:post key-handler}]
            ["active-search" {:post active-search-handler}]
            ["open-active-search" {:post open-active-search-handler}]
            ["close-active-search" {:post close-active-search-handler}]
            ["close-editor" {:post close-modal-handler}]
            ["select-child" {:post select-child-handler}]
            ["deselect-children" {:post deselect-children-handler}]]})
