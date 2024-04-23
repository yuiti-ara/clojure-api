(ns api.middleware.uuid
  (:require [clj-uuid :as uuid]))


(defn wrapper [router]
  (fn [request]
    (->
      (merge request {:uuid (uuid/v4)})
      router)))
