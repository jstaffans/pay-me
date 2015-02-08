(ns pay-me.model.event
  (:require [schema.core :as s]
            [clj-time.core :as ct]))

(defn datetime?
  [x]
  (.contains (.getName (.getClass x)) "DateTime"))

(def DateTime
  (s/pred datetime? 'datetime?))

(s/defrecord PaymentEvent
  [number    :- s/Str
   token     :- s/Str
   sum       :- s/Num
   result    :- s/Keyword
   timestamp :- DateTime])

(s/defn ^:always-validate payment-event :- PaymentEvent
  [number token sum result]
  (PaymentEvent. number token sum result (ct/now)))