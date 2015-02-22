(ns pay-me.endpoint.payment.core
  (:require [taoensso.timbre :refer [info error]]
            [clojure.core.async :as async]
            [pay-me.model.event :refer [payment-event]]))

; Simulates handling a credit card payment with an unreliable third-party service provider.
; A timeout channel is used as a guard.
(defn handle-payment [cc-handler report-chan]
  "Returns a function that handles payments using the given credit card service, reports to report-chan."
  (fn [number token]
    (async/go
      (let [sum (rand-int 5000)
            payment-chan (async/thread
                           (cc-handler token sum))
            timeout-chan (async/timeout 1200)
            result (async/alt!
                     payment-chan ([res] res)
                     timeout-chan ([_] :timeout))]

        ; Schema will validate the event when we try to put it on the channel because of the ^:always-validate
        ; metadata attached to the function that generates it. Reason: we don't want to leak bad data out of
        ; our namespace. Normally we would of course validate the data on input.
        (try
          (async/put! report-chan (payment-event number token sum result))
          result
          (catch Exception e
            (error e)
            :invalid))))))

