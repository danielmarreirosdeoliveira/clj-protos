(ns tsot-editor.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _a]
   (:active-panel db)))

(re-frame/reg-sub
 ::editor-contents
 (fn [db _]
   (:editor-contents db)))
