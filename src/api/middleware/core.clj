(ns api.middleware.core
  (:require [api.shared.loader :as loader]))


(def wrappers
  (reverse (loader/load-vars "src/api/middleware" :wrapper)))


(defn middleware [handler]
  ((apply comp wrappers) handler))
