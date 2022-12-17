(ns net.eighttrigrams.rocketoid.web.app.lhs)

(defn list-contexts [contexts]
  (map (fn [context]
         [:li (:title context)]) contexts))

(defn contexts-list-container-component [{:keys [contexts]}]
  [:#left-hand-side-container.side-container.lhs
   [:div.list-container
    [:ul
     (list-contexts contexts)]]])

(defn list-item-component [item]
  [:#left-hand-side-container.side-container.lhs
   [:div
    [:h1 (:title item)]
    [:p (:description item)]]])