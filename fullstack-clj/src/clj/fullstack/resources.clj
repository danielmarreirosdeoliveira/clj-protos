(ns fullstack.resources
  (:require [clojure.string :as str]))

(def resources
  [{:id 1 :name "one"}
   {:id 2 :name "two"}
   {:id 3 :name "three" :protected true}])

(defn get-resources [permissions query]
  {:result (->> resources
                (remove #(when (not= permissions "all") 
                           (:protected %)))
                (remove #(when (not= query "")
                           (not (str/starts-with? (:name %) (:q query))))))})
