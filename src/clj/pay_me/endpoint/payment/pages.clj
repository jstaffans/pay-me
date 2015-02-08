(ns pay-me.endpoint.payment.pages
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer [html5 include-js]]
            [hiccup.form :as f]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn- payment-form
  []
  [:div.payment-form
   (f/form-to
     {:id "pay-form" :name "pay-form"}
     [:post "/pay"]
     (f/text-field :number)
     (anti-forgery-field)
     [:a.pay-button {:id "pay-button"} "Pay!"])
   [:div.loader "Loading"]])

(defn layout
  [body cljs-init-ns]
  (html5
    [:html.payment
     {:lang "en"}
     [:head
      [:title "pay-me"]
      [:link
       {:href "/assets/normalize.css/normalize.css", :rel "stylesheet"}]
      [:link {:href "/css/site.css", :rel "stylesheet"}]]
     (conj body
           (include-js "/js/app.js")
           (when cljs-init-ns [:script (str cljs-init-ns ".init();")]))]))

(defn payment-page []
  (layout
    [:body
     [:h2 "Welcome to the Acme bank"]
     [:h3 "Why don't you go right ahead and enter your credit card number here and we'll charge a random amount:"]
     (payment-form)]
    "pay_me.form"))

(defn confirmation-page [result]
  (layout
    [:body
     [:h2 (str "Result = " result)]
     [:h3
      [:a {:href "/"} "Pay some more!"]]
     [:div.events
      [:table
       [:thead
        [:tr
         [:th "Number"] [:th "Sum"] [:th "Result"] [:th "Timestamp"]]]
       [:tbody]]]]
    "pay_me.confirm"))

