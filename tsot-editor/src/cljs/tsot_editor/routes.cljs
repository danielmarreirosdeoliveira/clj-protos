(ns tsot-editor.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [tsot-editor.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom
   ["/" {"context/" {[:id] :context}
         ""      :about}]))

(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [[handler id]]
   (if (nil? id)
     (bidi/path-for @routes handler)
     (bidi/path-for @routes handler :id id)))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route)) "-panel"))]
    (re-frame/dispatch [::events/set-active-panel [panel (get-in route [:route-params :id])]])))

(def history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (url-for handler)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn
    [handler]
    (navigate! handler)))
