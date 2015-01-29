(defproject pay-me "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.stuartsierra/component "0.2.2"]
                 [compojure "1.3.1"]
                 [duct "0.1.0"]
                 [environ "1.0.0"]
                 [meta-merge "0.1.1"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-json "0.3.1"]
                 [ring-jetty-component "0.2.2"]
                 [ring-webjars "0.1.0"]
                 [org.webjars/normalize.css "3.0.1"]
                 [org.webjars/jquery "2.1.3"]
                 [hiccup "1.0.5"]
                 [com.taoensso/timbre "3.3.1"]
                 [prismatic/schema "0.3.4"]
                 [clj-time "0.9.0"]
                 [puppetlabs/kitchensink "1.0.0"]]

  :plugins [[lein-environ "1.0.0"]
            [lein-gen "0.2.2"]
            [com.palletops/uberimage "0.4.1"]]
  :generators [[duct/generators "0.1.0"]]
  :duct {:ns-prefix pay-me}
  :main ^:skip-aot pay-me.main
  :aliases {"gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]}
  :uberimage {:base-image "tifayuki/java:8"
              :instructions "ENV PORT 3000"}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :dependencies [[reloaded.repl "0.1.0"]
                                  [org.clojure/tools.namespace "0.2.8"]
                                  [kerodon "0.5.0"]]
                   :env {:port 3000}}
   :project/test  {}})
