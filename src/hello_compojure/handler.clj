(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]))

(def known-sports {:tennis "tennis" :football "soccer"})

(defn get-sport [sport-id] 
  (println sport-id)
  {:status 200
    :body {:id (get known-sports (keyword sport-id) "UNKNOWN")}})

(defroutes app-routes
  (GET "/statistics/:sport-id" [sport-id] (get-sport sport-id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json-middleware/wrap-json-response)))
