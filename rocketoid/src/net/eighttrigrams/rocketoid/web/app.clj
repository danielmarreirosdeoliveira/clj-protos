(ns net.eighttrigrams.rocketoid.web.app
  (:require [net.eighttrigrams.rocketoid.ui :as ui]
            [net.eighttrigrams.rocketoid.datastore.search :as search]
            [net.eighttrigrams.rocketoid.web.app.lhs :as lhs]
            [net.eighttrigrams.rocketoid.web.app.rhs :as rhs]
            [net.eighttrigrams.rocketoid.web.app.script :as script]
            [net.eighttrigrams.rocketoid.web.app.modal-space :as modal-space]
            [xtdb.api :as xt]))

(defn component [{:keys [session biff/db anti-forgery-token query-params
                         net.eighttrigrams.rocketoid/ds]
                  :as   _req}]
  (let [{:user/keys [_email _foo _bar]} (xt/entity db (:uid session))
        issues                          (search/search-issues ds "")]
    [:div 
     [:div {:visibility :hidden} 
      (script/component "global-functions.js" anti-forgery-token)]
     (ui/page
      {}
      [:div
       {:hx-ws "connect:/ws"}
       [:div#app-container
        {;; TODO document recipe
         ;; to make the tab able to listen to key events, https://stackoverflow.com/a/3149416
         :tabindex 0}
        [:div#sides-container
         (lhs/contexts-list-container-component {:contexts issues})
         (rhs/component (search/search-issues ds "") 
                        (get query-params "q"))]]
       (modal-space/component)
       (script/component "app-container-keyhandler.js" anti-forgery-token)
       (script/app-container-focus-script-component (get query-params "q"))])]))
