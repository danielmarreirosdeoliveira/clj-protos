(ns tsot-editor.panels.editors
  (:require [quill :as quill]
            [reagent.core :as reagent]))

(defonce recently-added (atom {}))

(def quill-scoped-style
  ".ql-editor {
        width: 100%;
        margin: 10px;
        height: 100%;
        white-space: pre-wrap;
      }
      .ql-editor p {
        margin: 10px;
        padding-right: 15px;
      }
      .ql-editor:focus, input:focus {
        outline: none;
      }
      .ql-clipboard {
        visibility: hidden;
      }
      ::-webkit-input-placeholder {
        text-align: center;
      }
      :-moz-placeholder { /* Firefox 18- */
        text-align: center;  
      }
      ::-moz-placeholder {  /* Firefox 19+ */
        text-align: center;  
      }
      :-ms-input-placeholder {  
        text-align: center; 
      }")

(def button-height "22px")

(def style
  {:width            "400px"
   :border-top-width "1px"
   :border-top-style :solid
   :padding "5px"})

(def style:card
  {:width  "400px"
   :height "100%"})

(def style:buttons
  {:height button-height})

(defn- style:button
  [{background-color-light :background-color-light
    color            :color}]
  {:background-color    background-color-light
   :color               color
   :height              button-height
   :border-top-width    "0px"
   :border-left-width   "0px"
   :border-bottom-width "0px"})

(defn get-contents
  [editor]
  (js->clj
   (.-ops ^js/quill.Delta (.getContents editor))))

(defn- set-contents
  [editor content]
  (.setContents ^js/quill.Quill editor (clj->js content)))

(defn- card
  []
  (let [show-buttons (reagent/atom false)]
    (fn [styles !editors num-editors dispatch idx id]
      [:<>
       [:div {:style          style:card
              :on-mouse-enter #(reset! show-buttons true)
              :on-mouse-leave #(reset! show-buttons false)}
        (if (not @show-buttons)
          [:div {:style style:buttons} ""]
          [:<>
           (when (not= 0 idx) [:button {:on-click #(dispatch {:idx idx :type :move-up})
                                        :style (style:button styles)} "up"])
           [:button {:on-click #(do
                                  (swap! recently-added assoc id idx)
                                  (dispatch {:idx idx :type :insert-above}))
                     :style (style:button styles)} "ins-a"]
           (when (not= (dec num-editors) idx) [:button {:on-click #(dispatch {:idx idx :type :move-down})
                                                        :style (style:button styles)} "down"])
           [:button {:on-click #(do
                                  (swap! recently-added assoc id (inc idx))
                                  (dispatch {:idx idx :type :insert-below}))
                     :style (style:button styles)} "ins-b"]
           [:button {:on-click #(dispatch {:idx idx :type :delete-item})
                     :style (style:button styles)} "del"]])
        [:div
         {:id     (str "editor" idx)
          :on-click #(.focus (get @!editors idx))
          :style style}]]])))

(defn component
  [styles !editors num-editors dispatch id]
  [:div
   [:style
    quill-scoped-style]
   (when (= num-editors 0)
     [:button {:on-click #(do
                            (swap! recently-added assoc id 0)
                            (dispatch {:type :insert-top}))
               :style (style:button styles)} "+"])
   (map (fn [idx]
          ^{:key idx}
          [card styles !editors num-editors dispatch idx id])
        (range num-editors))])

(defn set-editors
  [editor editors editor-contents id on-mount?]
  (reset! editor (let [editor (quill. "#editor-main")]
                   (set-contents editor (get editor-contents 0))
                   (when on-mount? (.focus editor))
                   editor))
  (reset! editors
          (vec (map-indexed
                (fn [idx content]
                  (let [editor (quill. (str "#editor" idx))]

                    (when (and (contains? @recently-added id) ;; TODO regarding .focus and on-mount
                               (= idx (get @recently-added id)))
                      (swap! recently-added dissoc id)
                      (.focus editor))

                    (set-contents editor content)
                    editor)) (get editor-contents 1)))))
