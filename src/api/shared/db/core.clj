(ns api.shared.db.core
  (:require [datomic.api :as d]
            [api.shared.db.flip :as flip]
            [taoensso.timbre :as logger]))


(defn- log [data]
  (logger/info data)
  data)


; insert

(defn insert!
  ([schema conn payload] (insert! schema conn payload "temp-id"))
  ([schema conn payload id]
    (let [payload  (assoc payload :db/id id)
          _        (log payload)
          tx       (flip/schema->tx (d/db conn) payload)
          expr     (flip/tx->expr tx)
          _        (log tx)
          future   @(d/transact conn [tx])
          db-after (:db-after future)
          id-after (d/resolve-tempid db-after (:tempids future) id)]
      (log expr)
      (->
        (d/pull db-after expr (or id-after id))
        (dissoc :db/id)
        log
        (->>
          (flip/tx->schema schema db-after)
          log)))))


; pull

(defn- schema->expr [schema db]
  (->>
    schema
    (flip/schema->tx db)
    flip/tx->expr))


(defn pull [schema db id]
  (as->
    (schema->expr schema db) $
    (log $)
    (d/pull db $ id)
    (log $)
    (flip/tx->schema schema db $)
    (log $)))


(defn pull-many [schema db ids]
  (as->
    (schema->expr schema db) $
    (log $)
    (d/pull-many db $ ids)
    (log $)
    (map #(flip/tx->schema schema db %) $)
    (log $)))
