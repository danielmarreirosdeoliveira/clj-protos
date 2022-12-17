(ns net.eighttrigrams.rocketoid.web.router.commands
  (:require [rum.core :as rum]
            [ring.adapter.jetty9 :as jetty]
            [net.eighttrigrams.rocketoid.datastore :as datastore]
            [net.eighttrigrams.rocketoid.datastore.search :as datastore.search]
            [net.eighttrigrams.rocketoid.web.app.lhs :as lhs]
            [net.eighttrigrams.rocketoid.web.app.rhs :as rhs]
            [net.eighttrigrams.rocketoid.web.app.rhs.issues-list :as issues-list]
            [net.eighttrigrams.rocketoid.web.app.modal-space :as modal-space]))

(defn- send-component-via-websocket! [ws component & components]
  (if-not @ws
    (do (prn "RELOAD")
        500)
    (do (dorun
         (for [component (cons component components)]
           (jetty/send! @ws (rum/render-static-markup component))))
        200)))

(defn reload-issues!
  ([ws ds q current-id]
   (let [issues (datastore.search/search-issues ds q)]
     (send-component-via-websocket!
      ws
      (issues-list/component issues current-id)))))

(defn deselect-issues!
  [ws ds _state]
  (send-component-via-websocket!
   ws
   (lhs/contexts-list-container-component 
    {:contexts (datastore.search/search-issues ds "")})))

(defn select-child!
  [ws ds state]
  (let [item (datastore/get-issue-by-id ds (:currentId state))]
    (send-component-via-websocket!
     ws
     (lhs/list-item-component item))))

(defn open-modal!
  [ws anti-forgery-token key script-name]
  (send-component-via-websocket!
   ws
   (modal-space/mask-container-component anti-forgery-token key script-name)))

(defn close-modal!
  [ws ds]
  (send-component-via-websocket!
   ws
   (modal-space/mask-container-component nil nil nil))
  (reload-issues! ws ds "" nil))

(defn close-active-search! [ws ds state]
  (send-component-via-websocket!
   ws
   (rhs/active-search-mask-component)
   (rhs/active-search-container-component))
  (reload-issues! ws ds "" (:currentId state)))

(defn open-active-search!
  [ws anti-forgery-token]
  (send-component-via-websocket!
   ws
   (rhs/active-search-mask-component anti-forgery-token)
   (rhs/active-search-container-component anti-forgery-token)))
