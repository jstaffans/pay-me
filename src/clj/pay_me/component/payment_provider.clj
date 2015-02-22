(ns pay-me.component.payment-provider
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [info]]
            ))

(defrecord PaymentProvider [reporting]
  component/Lifecycle
  (start [component]
    (info "Payment provider started")
    (assoc component :cc-handler (fn [token sum]
                                        (info "Charging" sum "using token" token)
                                        (Thread/sleep (rand-int 2000))
                                        :ok)))
  (stop [component]
    (info "Payment provider stopped")
    (assoc component :cc-handler nil)))

; Two arities because we want to stub the configuration for testing.
; Normally, you would let the component system handle it.
(defn new-payment-provider
  ([] (new-payment-provider {}))
  ([config]
    (->PaymentProvider config)))