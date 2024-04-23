(ns api.middleware.db
  (:require [datomic.api :as d]
            [api.shared.config :as config]))


(defn wrapper [router]
  (fn [request]
    (let [conn (d/connect (:datomic-uri config/cfg))]
      (->>
        {:conn conn :db (d/db conn)}
        (merge request)
        router))))
