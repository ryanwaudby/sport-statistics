(ns hello-compojure.handler
  (:use [clojure.data.zip.xml])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(def menu-keys [:id :name])

(def counter-names
  {:tennis "sport.tennis.sports_statistics.page"})

(def known-sports
  {:tennis "tennis"})

(defn valid-sport? [sport-id]
  (contains? known-sports (keyword sport-id)))

(def full-priority-order-url
  "/Users/waudbr01/repos/hello-clojure/full-priority-order.xml")

(defn parse-xml-feed [feed-url]
  (zip/xml-zip (xml/parse feed-url)))

(def sports-data-file
  (parse-xml-feed full-priority-order-url))

(defn tournament-titles-in-full [sports-data]
  (xml-> sports-data :tournament
         :tournament-metadata
         :name (attr :full)))

(defn tournament-ids [sports-data]
  (xml-> sports-data :tournament
         :tournament-metadata (attr :tournament-key)))

(defn menu-nodes [sport-id]
  {:id "id"
   :name "name"
   :menu {}})

(defn sport-menu [sport-id]
  {:type "default"
   :defaultNodeId (first (tournament-ids sports-data-file))
   :nodes (menu-nodes sport-id)})

(defn sport-data-body [sport-id]
  {:id (get known-sports (keyword sport-id))
   :menu (sport-menu sport-id)
   :counterName (get counter-names (keyword sport-id))})

(def invalid-request {:status 400
                      :body {:message "unknown sport"}})

(defn sport-data [sport-id]
  (if (valid-sport? sport-id)
    {:body (sport-data-body sport-id)}
    invalid-request))

(defroutes app-routes
  (GET "/statistics/:sport-id" [sport-id] (sport-data sport-id))
  (route/not-found {:body {:message "invalid request"}}))

(def app
  (-> (handler/api app-routes)
      (json-middleware/wrap-json-response)))
