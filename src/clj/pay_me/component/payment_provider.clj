(ns pay-me.component.payment-provider
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [taoensso.timbre :refer [info error]]
            [pay-me.model.event :refer [payment-event]]))

; Actual method interacting with an imaginary credit card service.
; Stubbed out in unit tests.
(defn- charge-credit-card [number sum]
  (info "Charging" sum "from card number" number)
  (Thread/sleep (rand-int 2000))
  :ok)

; Simulate interaction with an unreliable third-party payment provider.
; A timeout channel is used as a guard. Returns a one-off channel that
; the result will be put on.
(defn- payment-handler [report-chan in-chan]
  (async/go
    (let [number (async/<! in-chan)
          sum (rand-int 5000)
          payment-chan (async/thread
                         (charge-credit-card number sum))
          timeout-chan (async/timeout 1200)
          result  (async/alt!
                    payment-chan ([res] res)
                    timeout-chan ([_] :timeout))]
      ; Schema will validate the event when we try to put it on the channel because of the ^:always-validate
      ; metadata attached to the function that generates it. Reason: we don't want to leak bad data out of
      ; our namespace. Normally we would of course validate the data on input.
      (try
        (async/put! report-chan (payment-event number sum result))
        result
        (catch Exception e
          (error e)
          :invalid)))))

(defrecord PaymentProvider [reporting]
  component/Lifecycle
  (start [component]
    (info "Payment provider started")
    ; Provide the payment handler as a partial function that expects an input channel as its completing argument.
    (assoc component :payment-handler (partial payment-handler (:report-chan reporting))))
  (stop [component]
    (info "Payment provider stopped")
    (assoc component :payment-handler nil)))

; Two arities because we want to stub the configuration for testing.
; Normally, you would let the component system handle it.
(defn new-payment-provider
  ([] (new-payment-provider {}))
  ([config]
    (->PaymentProvider config)))