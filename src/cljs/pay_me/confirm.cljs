(ns pay-me.confirm
  (:require [domina :as dom]
            [domina.css :refer [sel]]
            [ajax.core :refer [GET]]
            [cljs-time.coerce :as tc]
            [cljs-time.format :as tf]))

(def date-time-formatter
  (tf/formatter "dd.MM.yyyy HH:mm:ss"))

(defn payment-row [payment]
  (str "<tr>"
       "<td>" (:number payment) "</td>"
       "<td>" (:sum payment)    "</td>"
       "<td>" (:result payment) "</td>"
       "<td>" (->> (:timestamp payment) tc/from-long (tf/unparse date-time-formatter)) "</td>"
       "</tr>"))

(defn display-payments! [payments]
  (let [target (sel ".events table tbody")]
    (doseq [p payments]
      (dom/append! target (payment-row p)))))

(defn payments-handler [response]
  (display-payments! (take 10 response)))

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (GET "/status" {:handler payments-handler})))

