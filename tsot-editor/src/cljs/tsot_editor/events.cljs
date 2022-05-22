(ns tsot-editor.events
  (:require
   [re-frame.core :as re-frame]
   [tsot-editor.db :as db]
   [tsot-editor.rhs-handler :as rhs-handler]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::new
 (fn [{:keys [db]} [_ [from-id new-id editor-content editor-contents]]]
   (let [db (-> db
                (assoc-in [:editor-contents from-id 0] editor-content)
                (assoc-in [:editor-contents from-id 1] editor-contents)
                (assoc-in [:editor-contents new-id] [[] []]))]
     {:db db
      :navigate [:context new-id]})))

(re-frame/reg-event-fx
 ::delete
 (fn [{:keys [db]} [_ id]]
   (let [db (-> db
                (update-in [:editor-contents] dissoc id))
         selected-id (first (first (:editor-contents db)))]
     {:db db
      :navigate [:context selected-id]})))

(re-frame/reg-event-fx
 ::rename
 (fn [{:keys [db]} [_ [id to editor-content editor-contents]]]
   (let [db (-> db
                (update-in [:editor-contents] dissoc id)
                (assoc-in [:editor-contents to 0] editor-content)
                (assoc-in [:editor-contents to 1] editor-contents))]
     {:db db
      :navigate [:context to]})))

(re-frame/reg-event-fx
 ::navigate
 (fn [_ [_ handler]]
   {:navigate handler}))

(re-frame/reg-event-fx
 ::navigate-and-save
 (fn [{:keys [db]} [_ [from-id to-id editor-content editor-contents]]]
   (let [db (-> db
                (assoc-in [:editor-contents from-id 0] editor-content)
                (assoc-in [:editor-contents from-id 1] editor-contents))]
     {:db db
      :navigate [:context to-id]})))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn [{:keys [db]} [_ [active-panel id?]]]
   {:db (assoc db :active-panel [active-panel id?])}))

(re-frame/reg-event-fx
 ::handle-rhs-event
 (fn [{:keys [db]} [_ [{id :id :as event} lhs-content rhs-contents]]]
   (let [context rhs-contents
         context (rhs-handler/dispatch context event)
         db (assoc-in db [:editor-contents id 1] context)
         db (assoc-in db [:editor-contents id 0] lhs-content)]
     {:db db})))
