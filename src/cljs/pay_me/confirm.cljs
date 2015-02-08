(ns pay-me.confirm
  (:require [domina :as dom]
            [domina.css :refer [sel]]))

(defn display-payments! []
  (let [events (sel ".events table tbody")]
    (dom/append! events (str
                          "<tr>"
                          "<td>1234</td>"
                          "<td>12,34</td>"
                          "<td>:ok</td>"
                          "<td>timestamp</td>"))))

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (display-payments!)))

