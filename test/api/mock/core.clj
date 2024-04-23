(ns api.mock.core
  (:require [cheshire.core :as json]
            [ring.mock.request :as mock]
            [potemkin :refer [import-vars]]
            [api.mock.db])
  (:import (java.io ByteArrayInputStream)))


(import-vars [api.mock.db get-conn db])


(defn- byte-array->map [body]
  (json/parse-string (slurp body) true))


(defn- response [resp]
  (let [body (:body resp)]
    (if (= (type body) ByteArrayInputStream)
      (assoc resp :body (byte-array->map body))
      resp)))


(defn- request [method endpoint deps payload]
  (->
    (mock/request method endpoint)
    (mock/content-type "application/json")
    (mock/json-body payload)
    (merge deps)))


(defn get-resp
  ([router method endpoint deps]
    (get-resp router method endpoint deps {}))
  ([router method endpoint deps payload]
    (let [req (request method endpoint deps payload)]
      (let [resp (response (router req))]
        {:status (:status resp)
         :body   (:body resp)}))))
