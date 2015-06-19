(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] {:status 200
               :body {:name "hellow"
                      :desc "world"}})
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json-middleware/wrap-json-body {:keywords? true})
      (json-middleware/wrap-json-response)))
