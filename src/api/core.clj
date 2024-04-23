(ns api.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [api.middleware.core :as m]
            [api.router.core :as r]
            [api.shared.config :as config]))


(def handler
  (->
    r/router
    m/middleware))


(defn -main []
  (jetty/run-jetty handler {:port (:port config/cfg)}))
