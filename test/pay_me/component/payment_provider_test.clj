(ns pay-me.component.payment-provider-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as async]
            [pay-me.component.payment-provider :refer :all]
            [com.stuartsierra.component :as component]))

(def reporting-stub
  {:report-chan (async/chan)})

(def payment-provider (new-payment-provider reporting-stub))

(deftest payment-provider-test
  (alter-var-root #'payment-provider component/start)

  ; Stub the method that does the "actual" credit card charging
  (with-redefs [pay-me.component.payment-provider/charge-credit-card (fn [_ _] :charge-successful)]

    ; Test that payment provider attempts to charge the credit card we pass on its input channel
    (let [pay-chan (async/chan)
          payment-handler (:payment-handler payment-provider)
          result-chan (payment-handler pay-chan)]
      (async/>!! pay-chan "1234")
      (is (= :charge-successful (async/<!! result-chan))))

    ; Test that an event is put on the report channel
    (is (= :charge-successful (:result (async/<!! (:report-chan reporting-stub)))))))
