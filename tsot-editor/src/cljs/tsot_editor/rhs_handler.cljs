(ns tsot-editor.rhs-handler)

;; https://stackoverflow.com/a/18319708
(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn- insert-at-pos
  [pos coll ins]
  (vec (concat (subvec coll 0 pos) [ins] (subvec coll pos))))

(defn- move-up
  [context {idx :idx}]
  (let [idx (dec idx)
        top-item (get context idx)
        bottom-item (get context (inc idx))]
    (vec (concat (subvec context 0 idx) [bottom-item top-item] (subvec context (inc (inc idx)))))))

(defn- move-down
  [context {idx :idx}]
  (let [top-item (get context idx)
        bottom-item (get context (inc idx))]
    (vec (concat (subvec context 0 idx) [bottom-item top-item] (subvec context (inc (inc idx)))))))

(defn- insert-above
  [context {idx :idx}]
  (insert-at-pos idx context []))

(defn- insert-below
  [context {idx :idx}]
  (insert-at-pos (inc idx) context []))

(defn- delete-item
  [context {idx :idx}]
  (vec-remove idx context))

(defn- insert-top
  [context _]
  (vec (concat [[]] context)))

;; TODO generalize? convert atom to function name, for example
;; TODO alternativeley, use cond-> or cond->>
(defn dispatch
  [context {type :type :as event}]
  (cond (= :move-up type)
        (move-up context event)
        (= :move-down type)
        (move-down context event)
        (= :insert-above type)
        (insert-above context event)
        (= :insert-below type)
        (insert-below context event)
        (= :delete-item type)
        (delete-item context event)
        (= :insert-top type)
        (insert-top context event)))
