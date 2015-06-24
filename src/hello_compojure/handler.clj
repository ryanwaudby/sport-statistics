(ns hello-compojure.handler
  (:use [clojure.data.zip.xml])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]
            [clojure.xml :as xml]
            [clojure.zip :as zip]))

(defn parse-xml-feed [feed-url]
  (zip/xml-zip (xml/parse feed-url)))

(def full-priority-order-url
  "/Users/waudbr01/repos/hello-clojure/full-priority-order.xml")

(def sports-data-file
  (parse-xml-feed full-priority-order-url))


(def counter-names
  {:tennis "sport.tennis.sports_statistics.page"})

(def known-sports
  {:tennis "tennis"})

(defn valid-sport? [sport-id]
  (contains? known-sports (keyword sport-id)))

(defn tournament-title-from-id [id]
  (xml1-> sports-data-file
          :tournament
          :tournament-metadata [(attr= :tournament-key id)]
          :name (attr :full)))

(defn tournament-ids [sports-data]
  (xml-> sports-data
         :tournament
         :tournament-metadata (attr :tournament-key)))

(defn menu-node-from-id [id]
  {:id id
   :name (tournament-title-from-id id)
   :menu {}})

(defn menu-nodes [sport-id]
  (map menu-node-from-id
       (tournament-ids sports-data-file)))

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
