(ns tsot-editor.search.state-machine
  (:require [clojure.string :as str]))

(defn- initial
  []
  {:input:string ""
   :focused? false
   :suggestions []
   :selected-suggestion:id 0})

(defn- reset-state
  [state]
  (-> state
      (assoc :input:string "")
      (assoc :focused? false)))

(defn- select-above
  [state]
  (-> state
      (assoc :selected-suggestion:id
             (if (> (:selected-suggestion:id state) 0)
               (dec (:selected-suggestion:id state))
               (dec (count (:suggestions state)))))))

(defn- select-below
  [state]
  (-> state
      (assoc :selected-suggestion:id
             (if (< (:selected-suggestion:id state) (dec (count (:suggestions state))))
               (inc (:selected-suggestion:id state))
               0))))

(defn- make-suggestions
  ([make-suggestions:fn state] (make-suggestions make-suggestions:fn state nil))
  ([make-suggestions:fn
    state
    new-input:string]
   (let [{input:string :input:string :as state} state
         input:string (if new-input:string new-input:string input:string)
         suggestions (make-suggestions:fn input:string)]
     (-> state
         (assoc :input:string input:string)
         (assoc :focused? true)
         (assoc :suggestions suggestions)))))

(defn- adjust-if-necessary
  [{selected-suggestion:id :selected-suggestion:id
    old-suggestions        :suggestions} suggestions]
  (if
   (=
    (get old-suggestions selected-suggestion:id)
    (get suggestions selected-suggestion:id))
    selected-suggestion:id
    0))

(defn- suggest
  [make-suggestions:fn {old-input:string :input:string} {input:string :input:string}]
  (if (str/starts-with? input:string ":")
    (if (and (= 2 (count input:string)) (= 3 (count old-input:string)))
      ["" (make-suggestions:fn "")] ;; here we could also test for zero suggestions and go back immediately to ":n "
      [input:string (make-suggestions:fn input:string)])
    (let [suggestions (make-suggestions:fn input:string)]
      (if (zero? (count suggestions))
        [(str ":n " input:string) []]
        [input:string suggestions]))))

(defn- trim-string
  [input:string]
  (let [orig-input:string input:string
        input:string (str/replace input:string #"[^a-z\s:-]" "")]
    (if (str/starts-with? input:string ":")
      (if (and (> (count input:string) 2)
               (not= (get input:string 2) \space))
        (subs input:string 0 2)
        (if (and (> (count input:string) 1)
                 (not (get #{\d \r \n} (get input:string 1))))
          (subs input:string 0 1)
          (if (= (get input:string 1) \d)
            (if (not= (get orig-input:string 3) \?)
              (subs input:string 0 3)
              ":d ?")
            (if
             (> (count input:string) 2)
              (if (str/ends-with? input:string " ")
                (str (subs input:string 0 3) (str/replace (subs input:string 3) " " "-"))
                (if (str/ends-with? input:string ":")
                  (str (subs input:string 0 3) (str/replace (subs input:string 3) ":" ""))
                  input:string))
              input:string))))
      (str/replace (str/replace input:string " " "-") ":" ""))))

(defn- set-input:string
  [make-suggestions:fn
   state
   event]
  (let [event (update event :input:string trim-string)
        [input:string suggestions] (suggest make-suggestions:fn state event)
        selected-suggestion:id     (adjust-if-necessary state suggestions)]
    (-> state
        (assoc :input:string input:string)
        (assoc :suggestions suggestions)
        (assoc :selected-suggestion:id selected-suggestion:id))))

(defn- set-selected-suggestion:id
  [state idx]
  (assoc state :selected-suggestion:id idx))

(defn- reset-selected-suggestion:id
  [state]
  (set-selected-suggestion:id state 0))

(defn- enter
  [{suggestions            :suggestions
    input:string           :input:string
    selected-suggestion:id :selected-suggestion:id}
   {id :id}
   dispatch!]
  (if (pos? (count suggestions))
    (dispatch! {:type    :navigate-and-save
                :to-id   (get suggestions selected-suggestion:id)
                :from-id id})
    (do
      (when (and (str/starts-with? input:string ":n ") (> (count input:string) 3))
        (dispatch! {:type :new
                    :from-id id
                    :new-id   (str/replace input:string ":n " "")}))
      (when (and (str/starts-with? input:string ":d") (not (str/includes? input:string "?")))
        (dispatch! {:type :delete
                    :id   id}))
      (when (str/starts-with? input:string ":r ")
        (dispatch! {:type :rename
                    :id   id
                    :to   (str/replace input:string ":r " "")})))))

(defn create
  "Creates a state machine for handling the state of the search window"
  [make-suggestions:fn dispatch! atom]
  (let [!state     (atom nil)
        get-state #(deref !state)
        reset!    #(reset! !state %)
        dispatch! (fn transform! [event]
                    (let [state (get-state)]
                      (case (:type event)
                        :make-suggestions
                        (reset! (make-suggestions make-suggestions:fn state))
                        :start-search
                        (reset! (make-suggestions make-suggestions:fn state ""))
                        :start-rename
                        (reset! (make-suggestions make-suggestions:fn state ":r "))
                        :start-new
                        (reset! (make-suggestions make-suggestions:fn state ":n "))
                        :start-delete
                        (reset! (make-suggestions make-suggestions:fn state ":d ?"))
                        :reset-selected-suggestion:id
                        (reset! (reset-selected-suggestion:id state))
                        :set-input:string
                        (reset! (set-input:string make-suggestions:fn state event))
                        :reset
                        (reset! (reset-state state))
                        :select-below
                        (reset! (select-below state))
                        :select-above
                        (reset! (select-above state))
                        :enter
                        (enter state event dispatch!)
                        (dispatch! event))))]
    (reset! (initial))
    [get-state dispatch!]))
