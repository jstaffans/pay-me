(ns pay-me.endpoint.verification
  (:require [compojure.core :refer :all]))

(defn verification-endpoint [config]
  (routes
    ; emulates credit card verification: "verifies" it and responds with a secure token that the
    ; client can use for further transactions. Akin to Paymill.
    (POST "/verification" [] "a_secure_token")))
