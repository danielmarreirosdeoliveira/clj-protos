(ns ^:figwheel-hooks fullstack.ui
  (:require-macros [secretary.core :refer [defroute]])
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary]
   [accountant.core :as accountant]
   [reagent.dom :as rdom]
   [fullstack.login :as login]
   [fullstack.auth :as auth]
   [fullstack.resources :as resources]))

(defonce current (atom nil))

(defroute "/" []
  (reset! current :resources))
(defroute "/login" []
  (reset! current :auth))
(accountant/configure-navigation!
 {:nav-handler #(secretary/dispatch! %)
  :path-exists?  #(secretary/locate-route %)})
(accountant/dispatch-current!)

(defn- auth-info []
  @auth/token-atom
  (fn []
     (if (= @auth/token-atom "")
       [:div [:span "role: anonymous | "] [:a {:href "/login"} "log in"]]
       [:div [:span "role: admin | "] [:span {:style {:color :blue :font-weight :bold} :on-click auth/log-out} "log out"]])))

(defn- resources []
  [:div
   [:div {:style {:position :absolute
                  :right "10px"
                  :top "8px"}}
    [auth-info]]
   [:h1 "Resources"]
   [resources/component]])

(defn- auth []
  [:div 
   [:h1 "Auth"]
   [login/component]])

(defmulti current-page #(deref current))
(defmethod current-page :resources []
  [resources])
(defmethod current-page :auth []
  [auth])
(defmethod current-page :default []
  [:div])

(defn get-app-element []
  (gdom/getElement "app"))
(defn mount [el]
  (rdom/render [current-page] el))
(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))
;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)
;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
