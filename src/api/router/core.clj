(ns api.router.core
  (:require [compojure.route :as route]
            [compojure.api.sweet :as api]
            [compojure.api.coercion.schema :as sc]
            [compojure.api.exception :as ex]
            [ring.middleware.reload :as reload]
            [taoensso.timbre :as logger]
            [api.shared.loader :as loader]
            [api.shared.coercer :as coercer]
            [api.shared.config :as config]))


(defn- expose-param! [kw]
  "Expose custom request parameters in compojure-api router"
  (defmethod compojure.api.meta/restructure-param kw [_ binding acc]
    (update-in acc [:letks] into [binding (conj `(~'+compojure-api-request+) kw)])))


(def coercion
  (let [opts {:string   {:default sc/string-coercion-matcher}
              :body     {:formats {"application/json" (coercer/wrap-add-prefix sc/json-coercion-matcher)}}
              :response {:default (constantly nil)}}]
    (sc/create-coercion opts)))


(def wrap-reload
  (if (:api-debug config/cfg)
    reload/wrap-reload))


(defn log-exceptions [exception & _]
  (logger/info (with-out-str (clojure.pprint/pprint exception)))
  (throw exception))


(def router
  (do
    (expose-param! :uuid)
    (expose-param! :db)
    (expose-param! :conn)
    (expose-param! :identity)
    (api/api
      {:coercion coercion
       :middleware [wrap-reload coercer/wrap-remove-prefix]
       :exceptions{:handlers {::ex/default log-exceptions}}}
      (loader/load-vars "src/api/router" :routes)
      (route/not-found {:status 404 :message "not found"}))))
