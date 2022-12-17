(ns net.eighttrigrams.rocketoid.web.app.rhs
  (:require [net.eighttrigrams.rocketoid.web.app.script :as script]
            [net.eighttrigrams.rocketoid.web.app.rhs.issues-list :as issues-list]))

(defn active-search-container-component
  ([]
   [:#active-search-input-container])
  ([anti-forgery-token]
   [:#active-search-input-container
    [:input#active-search
     {:autocomplete :off}]
    (script/component "search-input.js" anti-forgery-token)]))

(defn active-search-mask-component
  ([]
   [:div#list-mask-container
    {:_ "init remove .search-active from #issues-list-container"}])
  ([_]
   [:div#list-mask-container
    {:_ "init add .search-active to #issues-list-container
         on click call closeActiveSearchAndRevertSelection()"}
    [:div.mask.mask-active-search]]))

(defn component
  [state selected-issue-id]
  [:#right-hand-side-container.side-container
   (active-search-container-component)
   [:div#issues-list-container.list-container
    (issues-list/component state selected-issue-id)]
   (active-search-mask-component)])
