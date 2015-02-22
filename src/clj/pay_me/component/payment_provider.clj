(ns pay-me.component.payment-provider
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [info]]
            ))

(defrecord PaymentProvider []
  component/Lifecycle
  (start [component]
    (info "Payment provider started")
    ; A dummy credit card payment handler
    (assoc component :cc-handler (fn [token sum]
                                        (info "Charging" sum "using token" token)
                                        (Thread/sleep (rand-int 2000))
                                        :ok)))
  (stop [component]
    (info "Payment provider stopped")
    (assoc component :cc-handler nil)))

(defn new-payment-provider
  []
  (PaymentProvider.))