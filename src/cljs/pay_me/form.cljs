(ns pay-me.form
  (:require [domina :as dom]
            [domina.events :as ev]
            [ajax.core :refer [POST]]
            [cljs.core.async :refer [>! chan <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn obfuscate [number]
  "Takes a credit card number and garbles a portion of it."
  (clojure.string/replace number #"\B." "*"))


(defn get-token-and-submit! []
  (let [c (chan)]
    (POST "/verification" {:handler (fn [response] (go (>! c response)))})
    (go
      (let [response (<! c)
            number-field (dom/by-id "number")]
        (dom/append! (dom/by-id "pay-form") "<input id='token' type='hidden' name='token'/>")
        (dom/set-value! (dom/by-id "token") response)
        (dom/set-value! number-field (obfuscate (dom/value number-field)))
        (.submit (dom/by-id "pay-form"))))))

(defn submit-payment-form! []
  (do
    (dom/set-style! (dom/by-class "loader") "display" "block")
    (get-token-and-submit!)))

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (ev/listen! (dom/by-id "pay-button") :click submit-payment-form!)))


