(ns hello-compojure.handler
  (:use [clojure.data.zip.xml])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(def known-sports {:tennis "tennis" :football "soccer"})

(defn tournament-titles [sports-data] (xml-> sports-data :tournament :tournament-metadata :name (attr :full)))

(def sports-data-file (zip/xml-zip (xml/parse "/Users/waudbr01/repos/hello-clojure/full-priority-order.xml")))

(defn sport-body [sport-id] {:id (get known-sports (keyword sport-id) "unknown sport")
                             :events (tournament-titles sports-data-file) })

(defn get-sport [sport-id] {:status 200 :body (sport-body sport-id) })

(defroutes app-routes
  (GET "/statistics/:sport-id" [sport-id] (get-sport sport-id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json-middleware/wrap-json-response)))
