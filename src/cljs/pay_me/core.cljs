(ns pay-me.core
  (:require [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(secretary/defroute "/" []
  (js/console.log "Home"))

;
; secretary boilerplate
;
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn init! []
  (hook-browser-navigation!))

(init!)