(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.core.async :as async]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [meta-merge.core :refer [meta-merge]]
            [reloaded.repl :refer [system init start stop go reset]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [pay-me.config :as config]
            [pay-me.system :as system]))

(def dev-config
  {:app {:middleware [wrap-stacktrace]}})

(def config
  (meta-merge config/defaults
              config/environ
              dev-config))

(when (io/resource "local.clj")
  (load "local"))

(reloaded.repl/set-init! #(system/new-system config))

