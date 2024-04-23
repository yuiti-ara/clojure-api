(ns api.middleware.logger.core
  (:require [clojure.string :as str]
            [ring.util.request :as req]
            [cheshire.core :as json]
            [taoensso.timbre :as logger]
            [api.middleware.logger.setup :as setup])
  (:import (com.fasterxml.jackson.core JsonParseException)))


(defn- format-body [body]
  (if
    (and
      (not (empty? body))
      (str/starts-with? body "{"))
    (try
      (json/parse-string body true)
      (catch JsonParseException _ body))
    body))


(defn- log-return [http-msg]
  (do
    (if-let [method (-> http-msg :request-method str not-empty)]
      (let [ip          (-> http-msg :remote-addr)
            uri         (-> http-msg :uri not-empty)
            query-param (-> http-msg :query-string)
            body        (-> http-msg :body format-body)]
        (logger/info ip :request method uri query-param body))
      (if-let [status (-> http-msg :status)]
        (let [body    (-> http-msg :body format-body)]
          (logger/info :response status body))))
    http-msg))


(defn- parse-body [http-msg]
  (->>
    (req/body-string http-msg)
    (assoc http-msg :body)))


(defn wrapper [handler]
  (do
    (setup/setup-logger!)
    (fn [request]
      (->
        request
        parse-body
        log-return
        handler
        parse-body
        log-return))))
