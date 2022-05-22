(ns tsot-editor.search.suggestions
  (:require [clojure.string :as str]))

(defn make
  [editor-contents content id maximum]
  (->> (keys editor-contents)
       (filter #(not= id %))
       (filter #(str/starts-with? (str/lower-case %) (str/lower-case content)))
       (take maximum)
       (vec)))
