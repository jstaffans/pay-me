(ns pay-me.system
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [pay-me.component.reporting :refer [reporting]]
            [pay-me.component.payment-provider :refer [new-payment-provider]]
            [pay-me.endpoint.payment :refer [payment-endpoint]]))

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-webjars]
                      [wrap-defaults :defaults]]
         :not-found  (io/resource "errors/404.html")
         :defaults   site-defaults}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
          :payment-provider   (new-payment-provider)
          :reporting          (reporting)
          :app                (handler-component (:app config))
          :http               (jetty-server (:http config))
          :payment            (endpoint-component payment-endpoint))
        (component/system-using
          {:http              [:app]
           :app               [:payment]
           :payment-provider  [:reporting]
           :reporting         []
           :payment           [:payment-provider :reporting]}))))
