(ns tsot-editor.views
  (:require
   [re-frame.core :as re-frame]
   [tsot-editor.events :as events]
   [tsot-editor.routes :as routes]
   [tsot-editor.subs :as subs]
   [tsot-editor.panels.context :as context]))

(def styles
  (let [background-color-dark "#002b36"]
    {:background-color-light "#073642"
     :background-color-dark  background-color-dark
     :hover-color            background-color-dark
     :border-color           "#839496"
     :color                  "#b58900"
     :font-family            "Lucida Console, Courier New, monospace"
     :border-width           "1px"
     :border-radius         "12px"}))

(defmethod routes/panels :context-panel [_ id] [context/component styles id])

(defn about-panel []
  (let [editor-contents (re-frame/subscribe [::subs/editor-contents])]
    [:div
     {:style {:z-index 1005
              :position :relative}}
     (doall (map (fn [k]
                   ^{:key k}
                   [:div [:a {:on-click #(re-frame/dispatch [::events/navigate [:context k]])}
                          (str "go to context " k)]
                    [:br]
                    [:br]])
                 (keys @editor-contents)))]))

(defmethod routes/panels :about-panel [] [about-panel])

(defn main-panel []
  [:div
   [:h1 {:style {:text-align :center
                 :z-index 1001
                 :right "16px"
                 :top "18px"
                 :position         :relative
                 :margin           "0 auto"}} "tsot-editor.prototype"]
   (let [active-panel (re-frame/subscribe [::subs/active-panel])
         [panel id] @active-panel]
     (routes/panels panel id))])
