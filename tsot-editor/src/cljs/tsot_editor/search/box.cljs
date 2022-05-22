(ns tsot-editor.search.box)

(defonce padding "10px")

(defn- search-suggestion-item-scoped-style
  [{hover-color :hover-color
    border-radius :border-radius}]
  (str "
  .suggestion {
      border-radius: 0px;
  }
  .suggestion:hover { 
      background-color: " hover-color "; 
  }
  .suggestion.last {
      border-radius: 0px 0px " border-radius " " border-radius ";
  }"))

(defn- style:suggestion-item
  [{hover-color :hover-color
    border-radius :border-radius}
   {suggestions            :suggestions
    selected-suggestion:id :selected-suggestion:id}
   idx]
  (let [highlighted? (= idx selected-suggestion:id)
        last? (= idx (dec (count suggestions)))]
    (merge
     {:margin-top     "0px"
      :margin-bottom  "0px"
      :padding-left   "9px"
      :padding-top padding
      :border-radius (when (and highlighted? last?)
                       (str "0px" " " "0px" " " border-radius " " border-radius))
      :padding-bottom padding}
     (if highlighted? {:background-color hover-color} {}))))

(defn- make-border-radius
  [border-radius
   {focused? :focused?
    suggestions :suggestions}]
  (let [bottom-radius (if (and focused? (pos? (count suggestions))) "0px" border-radius)]
    (str border-radius " " border-radius " "
         bottom-radius " " bottom-radius)))

(defn- style:search-container
  [{background-color :background-color
    border-width :border-width
    border-radius :border-radius}
   state]
  {:width            "50%"
   :position         :relative
   :margin           "0 auto"
   :top              "95px"
   :z-index          "1005"
   :padding          "0px"
   :border-radius    (make-border-radius border-radius state)
   :border-style     :solid
   :border-width     border-width
   :max-width        "600px"
   :background-color background-color})

(defn- style:search-input
  [{background-color-light :background-color-light
    font-family            :font-family
    border-radius          :border-radius
    color                  :color}
   {focused? :focused? :as state}]
  (merge
   {:width            "97%"
    :max-width        "579px"
    :border-style     :none
    :font-size        "16px"
    :border-radius    (make-border-radius border-radius state)
    :padding          padding
    :font-family      font-family
    :background-color background-color-light
    :color            color}
   (if focused? {:cursor :none} {})))

(defn- search-suggestions-style
  [{background-color-light :background-color-light
    border-color :border-color
    border-width :border-width
    border-radius :border-radius
    font-family :font-family}]
  {:position         :absolute
   :left             (str "-" border-width)
   :right            (str "-" border-width)
   :z-index          "1005"
   :font-family font-family
   :border-style     :solid
   :border-radius    (str "0px" " " "0px" " " border-radius " " border-radius)
   :border-width     border-width
   :padding-left     border-width
   :padding-right    border-width
   :opacity          "0.82"
   :border-top-style :dotted
   :border-top-color border-color
   :background-color background-color-light})

(defn- suggestion-to-item
  [id styles {suggestions :suggestions :as state} dispatch!]
  (fn
    [idx key]
    ^{:key (str "item-" key)}
    [:p.suggestion
     {:class          (when (= idx (dec (count suggestions))) "last")
      :style          (style:suggestion-item styles state idx)
      :on-click       #(dispatch! {:type    :navigate-and-save
                                   :to-id   key
                                   :from-id id})} key]))

(defn- component:suggestions
  [styles
   {id :id} 
   {suggestions :suggestions
    :as         state}
   dispatch!]
  (fn []
    [:div
     {:style (search-suggestions-style styles)}
     [:<>
      [:style (search-suggestion-item-scoped-style styles)]
      (doall (map-indexed (suggestion-to-item id styles state dispatch!) suggestions))]]))

(defn- component
  [{styles :styles
    id :id
    :as context}
   [get-state dispatch!]]
  (let [{input:string :input:string
         focused? :focused?
         suggestions :suggestions :as state} (get-state)]
    [:div {:style (style:search-container styles state)}
     [:input {:on-focus    #(when (not focused?) (dispatch! {:type :make-suggestions}))
              :on-change   #(dispatch! {:type :set-input:string :input:string (-> % .-target .-value)})
              :value       input:string
              :placeholder (if focused? "" id)
              :style       (style:search-input styles state)}]
     (when (and focused? (not= 0 (count suggestions)))
       [component:suggestions styles context state dispatch!])]))
