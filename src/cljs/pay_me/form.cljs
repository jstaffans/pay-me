(ns pay-me.form
  (:require [domina :as dom]
            [domina.events :as ev]) )

(defn submit-payment-form! []
  (do
    (dom/set-style! (dom/by-class "loader") "display" "block")
    (.submit (dom/by-id "pay-form"))))

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (ev/listen! (dom/by-id "pay-button") :click submit-payment-form!)))



