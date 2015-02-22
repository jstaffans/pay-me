(ns pay-me.endpoint.payment
  (:require [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [clojure.core.async :as async]
            [cognitect.transit :as transit]
            [clj-time.coerce :as coerce]
            [ring.util.response :refer [response content-type]]
            [compojure.core :refer :all]
            [pay-me.endpoint.payment.pages :as pages]
            [pay-me.endpoint.payment.core :refer [handle-payment]])
  (:import (org.joda.time DateTime)))

(defn site-routes [config]
  (-> (routes
        (GET "/" [] (pages/payment-page))
        (POST "/pay" [number token]
          (let [cc-handler (get-in config [:payment-provider :cc-handler])
                report-chan (get-in config [:reporting :report-chan])
                payment-result (handle-payment cc-handler report-chan)]
            (pages/confirmation-page (async/<!! (payment-result number token))))))

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
