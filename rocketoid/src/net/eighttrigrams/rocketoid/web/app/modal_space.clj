(ns net.eighttrigrams.rocketoid.web.app.modal-space
  (:require [net.eighttrigrams.rocketoid.web.app.script :as script]))

(defn mask-component
  [anti-forgery-token key script]
  [:div.mask
   [:#modal-container
    [key]
    (script/component script anti-forgery-token)]])

(defn component
  []
  [:div#modal-space
   [:div#mask-container]])

(defn mask-container-component 
  [anti-forgery-token key script-name]
  [:div#mask-container
   (when anti-forgery-token
     (mask-component anti-forgery-token key
                     script-name))])
