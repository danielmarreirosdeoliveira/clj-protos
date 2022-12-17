(ns net.eighttrigrams.rocketoid.web.app.rhs.issues-list)

(defn component [issues _selected-issue-id]
  [:ul#active-search-list
   (map (fn [issue]
          [:li
           {:id (:id issue)
            ;; :class (when (= selected-issue-id (:id issue)) "selected")
            :_ (format "on click 
                        call 
                          selectIssueByIdAndCloseActiveSearchIfOpen('%s') 
                        then 
                          add .selected
                        end" (:id issue))}
           (:title issue)])
        issues)])
