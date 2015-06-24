(ns hello-compojure.handler
  (:use [clojure.data.zip.xml])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(def known-sports
  {:tennis "tennis"})

(def full-priority-order-url
  "/Users/waudbr01/repos/hello-clojure/full-priority-order.xml")

(defn parse-xml-feed [feed-url]
  (zip/xml-zip (xml/parse feed-url)))

(def sports-data-file
  (parse-xml-feed full-priority-order-url))

(defn tournament-titles [sports-data]
  (xml-> sports-data :tournament
         :tournament-metadata
         :name (attr :full)))

(defn sport-data-body [sport-id]
  {:id (get known-sports (keyword sport-id) "unknown sport")
   :events (tournament-titles sports-data-file)})

(defn sport-data [sport-id]
  {:status 200 :body (sport-data-body sport-id)})

(defroutes app-routes
  (GET "/statistics/:sport-id" [sport-id] (sport-data sport-id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json-middleware/wrap-json-response)))
