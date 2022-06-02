(ns fullstack.utils) 

;; right now this is here only for demo purposes

(defn multiply [a b] (* a b))

(defmacro infix
  [infixed]
  (list (second infixed) 
        (first infixed) 
        (last infixed)))
