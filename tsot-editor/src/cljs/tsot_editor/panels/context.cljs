(ns tsot-editor.panels.context
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [tsot-editor.events :as events]
   [tsot-editor.subs :as subs]
   [tsot-editor.panels.editors :as editors]
   [tsot-editor.search.suggestions :as suggestions]
   [tsot-editor.search.search :as search]))

(def max-suggestions 10)

(defn style:context-component
  [{background-color-dark :background-color-dark}]
  {:width "100%"
   :height "100%"
   :background-color background-color-dark
   :position :fixed
   :top 0
   :left 0})

(defn style:editors-container
  [{font-family :font-family}]
  {:width            "66%"
   :font-family font-family
   :position         :fixed
   :transform        "translate(25%, 0)"
   :overflow-y       :auto
   :scrollbar-width  :none
   :top              "116px"
   :bottom           "100px"
   :border-top-width "1px"
   :border-top-style :solid})

(def style:editor-container-inner
  {:position :relative
   :top      "20px"})

(def style:lhs-editor-container
  {:width              "50%"
   :border-right-style :solid
   :border-right-width "1px"
   :min-height "200px"
   :left "-1px"
   :position           :absolute})

(def style:rhs-editors-container
  {:width "50%"
   :position :absolute
   :border-left-style :solid
   :border-left-width "1px"
   :right 0})

(defn- component:editor-container-inner-scoped-style
  [border-color color]
  [:<>
   [:style (str "div { border-color: " border-color "; }")]
   [:style (str "div { color: " color "; }")]])

(def style:rhs {:style style:rhs-editors-container})

(defn- dispatch-editor-event
  [id
   {type :type
    :as  %}
   editor
   editors]
  ;; TODO clean up
  (cond
    (= type :save-contents) (re-frame/dispatch [::events/set-contents [id (:idx %) (:data %)]])
    :else (re-frame/dispatch [::events/handle-rhs-event [(assoc % :id id)
                                                         (editors/get-contents @editor)
                                                         (mapv editors/get-contents @editors)]])))

(defn- component:editor-container-inner
  [{id                                                     :id
    {border-color :border-color
     color        :color
     :as          styles} :styles}
   editor
editors
   !editor-contents]
  (fn []
    [:div {:style style:editor-container-inner}
     [component:editor-container-inner-scoped-style border-color color]
     [:div#editor-main
      {:on-click #(.focus @editor)
       :style  style:lhs-editor-container}]
     [:div.rhs
      style:rhs
      (editors/component
       styles
       editors
       (count (get-in @!editor-contents [id 1]))
       #(dispatch-editor-event id % editor editors)
       id)]]))

;; TODO clean up, maybe use spec
(defn create-dispatch [editor editors]
  (fn dispatch! [{from-id :from-id
                  to-id   :to-id
                  type   :type
                  :as event}]
    (when (= type :new)
      (re-frame/dispatch
       [::events/new [(:from-id event)
                      (:new-id event)
                      (editors/get-contents @editor)
                      (mapv editors/get-contents @editors)]]))
    (when (= type :delete)
      (re-frame/dispatch
       [::events/delete (:id event)]))
    (when (= type :rename)
      (re-frame/dispatch
       [::events/rename
        [(:id event)
         (:to event)
         (editors/get-contents @editor)
         (mapv editors/get-contents @editors)]]))
    (when (= type :navigate-and-save)
      (re-frame/dispatch
       [::events/navigate-and-save
        [from-id
         to-id
         (editors/get-contents @editor)
         (mapv editors/get-contents @editors)]]))))

(defn- render:component [{styles :styles id :id :as context}
                         editor
                         editors
                         !editor-contents]
  @!editor-contents
  (fn []
    [:div {:style (style:context-component styles)}
     [search/component
      (-> context
          (assoc :editor-contents @!editor-contents)
          (assoc :dispatch! (create-dispatch editor editors)))
      #(suggestions/make @!editor-contents % id max-suggestions)]
     [:div
      {:style (style:editors-container styles)}
      [component:editor-container-inner context editor editors !editor-contents]]]))

(defn component [styles id]
  (let [editor-contents (re-frame/subscribe [::subs/editor-contents])
        editor (atom nil)
        editors (atom nil)]
    [(reagent/create-class
      {:component-did-mount #(editors/set-editors editor editors (get @editor-contents id) id true)
       :component-did-update #(editors/set-editors editor editors (get @editor-contents id) id false)
       :reagent-render  (render:component
                         {:styles styles :id id}
                         editor
                         editors
                         editor-contents)})]))
