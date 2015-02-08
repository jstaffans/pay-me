(ns pay-me.endpoint.payment
  (:require [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [cognitect.transit :as transit]
            [clj-time.coerce :as coerce]
            [ring.util.response :refer [response content-type]]
            [compojure.core :refer :all]
            [clojure.core.async :as async]
            [pay-me.endpoint.payment.pages :as pages])
  (:import (org.joda.time DateTime)))

(defn site-routes [config]
  (-> (routes
        (GET "/" [] (pages/payment-page))
        (POST "/pay" [number token]
          (let [payment-handler (get-in config [:payment-provider :payment-handler])
                payment-chan (async/chan)
                result-chan (payment-handler payment-chan)]
            (async/>!! payment-chan {:number number :token token})
            (pages/confirmation-page (async/<!! result-chan)))))

      ; wrap the payment form with anti-forgery, but don't interfere with other endpoints using POST
      (wrap-routes wrap-anti-forgery)))

(defn api-routes [config]
  (routes
    (GET "/status" []
      (let [events @(:events (:reporting config))]
        (response events)))))

(defn transit-routes [config]
  "Wraps api routes with Transit boilerplate stuff."
  (let [joda-time-writer (transit/write-handler
                           (constantly "m")
                           #(-> % coerce/to-date .getTime)
                           #(-> % coerce/to-date .getTime .toString))]
    (wrap-transit-response
      (api-routes config)
      {:opts {:handlers {DateTime joda-time-writer}}})))

(defn payment-endpoint [config]
  (routes
    (site-routes config)
    (transit-routes config)))
