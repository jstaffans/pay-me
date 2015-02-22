(ns pay-me.component.reporting
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [taoensso.timbre :refer [info]]))

(defrecord Reporting [storage]
  component/Lifecycle

  (start [component]
    (info "Reporting started")
    (let [ctrl-chan (async/chan)
          ; The channel reports are received on.
          ; We don't care if we miss a few so make it use a dropping buffer.
          report-chan (async/chan (async/dropping-buffer 10))]
      (assoc component
        :report-chan report-chan

        ; A loop that polls the reporting channel for new events
        :loop-chan (async/go
                     (loop []
                       (let [[event _] (async/alts! [report-chan ctrl-chan])]
                         (when (not= ::stop event)
                           (do
                             (swap! (:storage component) conj event)
                             (recur)))))
                     (info "Closing loop channel"))

        ; The control channel is used to stop the above loop channel when the system stops.
        ; The cost of NOT doing so is actually negligible (one parked goroutine).
        :ctrl-chan ctrl-chan

        ; Returns the currently stored events.
        :events (:storage component))))

  (stop [component]
    (info "Reporting stopped")
    (async/close! (:report-chan component))
    (async/>!! (:ctrl-chan component) ::stop)
    (reset! (:storage component) (atom ()))
    (assoc component
      :report-chan nil
      :loop-chan nil
      :events nil)))

; Create a reporting component with a simple atom as the backing storage.
; Normally we would use a database of some sort.
(defn new-reporting []
  (->Reporting (atom ())))
