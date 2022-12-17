(ns net.eighttrigrams.rocketoid.web.app.script
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; TODO implement caching, activated when in prod mode

(defn script* [html]
  [:script {:dangerouslySetInnerHTML {:__html html}}])

;; TODO mabe rename ns to script and rename make-script to component
(defn component [script-name anti-forgery-token]
  (prn script-name)
  (script* (str/replace 
            (slurp (io/resource (str "js/" script-name))) 
            "ANTI_FORGERY_TOKEN" anti-forgery-token)))

(defn app-container-focus-script-component [current-id]
  ;; TODO put into script file
  (script* (str
            "var url = new URL(window.location.href).origin
                     window.history.pushState({}, '', url)
                     "
            (if-not current-id 
              "appContainer.focus()"
              (str "
                     appContainer.focus()
                     window.state.currentId = '" current-id "'
                     document.getElementById(window.state.currentId).scrollIntoView()
                    ")))))
