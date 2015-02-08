(ns pay-me.form
  (:require [domina :as dom]
            [domina.events :as ev]
            [ajax.core :refer [POST]]))

(defn obfuscate [number]
  "Takes a credit card number and garbles a portion of it."
  (clojure.string/replace number #"\B." "*"))

(defn get-token-and-submit! []
  (POST "/verification" {:handler (fn [response]
                                    (let [number-field (dom/by-id "number")]
                                      (dom/append! (dom/by-id "pay-form") "<input id='token' type='hidden' name='token'/>")
                                      (dom/set-value! (dom/by-id "token") response)
                                      (dom/set-value! number-field (obfuscate (dom/value number-field)))
                                      (.submit (dom/by-id "pay-form"))))}))

(defn submit-payment-form! []
  (do
    (dom/set-style! (dom/by-class "loader") "display" "block")
    (get-token-and-submit!)))

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (ev/listen! (dom/by-id "pay-button") :click submit-payment-form!)))



