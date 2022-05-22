(ns tsot-editor.search.search
  (:require [reagent.core :as reagent]
            [tsot-editor.search.box :as box]
            [tsot-editor.search.state-machine :as state-machine]))

(def style:mask
  {:position         :fixed
   :top              "0px"
   :left             "0px"
   :width            "100%"
   :height           "100%"
   :background-color :black
   :z-index          "1000"
   :opacity          "0.55"})

(defn- key-listener
  [{id        :id}
   [get-state dispatch!]]
  (fn [event]
    (let [state                (get-state)
          {focused? :focused?} state
          key                  (.-key event)]
      (when (get #{"ArrowUp" "ArrowDown"} key)
        (.stopPropagation event))
      (when focused?
        (case key
          "ArrowUp"
          (dispatch! {:type :select-above})
          "ArrowDown"
          (dispatch! {:type :select-below})
          "Escape"
          (dispatch! {:type :reset})
          "Enter"
          (dispatch! {:type :enter
                            :id   id})
          nil))
      (case key
        "F1"
        (dispatch! {:type :start-new})
        "F8"
        (dispatch! {:type :start-search})
        "F2"
        (dispatch! {:type :start-rename})
        "F4"
        (dispatch! {:type :start-delete})
        nil))))

(defonce event-listener-added? (atom false))
(defonce event-listener (atom nil))

(defn- set-up-listener
  [context state-machine]
  (when (not @event-listener-added?)
    (when @event-listener (.removeEventListener (.querySelector js/document "body") "keydown" @event-listener))
    (reset! event-listener-added? true)
    (reset! event-listener (key-listener context state-machine))
    (.addEventListener (.querySelector js/document "body") "keydown" @event-listener)))

(defn- component:mask
  [dispatch!]
  [:div {:on-click #(dispatch! {:type :reset})
         :style    style:mask} ""])

(defn- render
  [context [get-state dispatch! :as state-machine]]
  #(do (when
        (<= (count (:suggestions (get-state))) (:selected-suggestion:id (get-state)))
         (dispatch! {:type :reset-selected-suggestion:id}))
       [box/component context state-machine]))

(defn- when-mounted
  [context [get-state :as state-machine]]
  #(do
     (set-up-listener context state-machine)
     (when (:focused? (get-state)) (.focus (.querySelector js/document "input")))))

(defn component
  [{dispatch! :dispatch!} make-suggestions]
  (reset! event-listener-added? false)
  (let [[get-state dispatch! :as state-machine] (state-machine/create make-suggestions dispatch! reagent/atom)]
    (fn [context _]
      [:<>
       (when (:focused? (get-state)) [component:mask dispatch!])
       [(reagent/create-class
         {:component-did-mount (when-mounted context state-machine)
          :reagent-render      (render context state-machine)})]])))
