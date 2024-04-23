(ns api.shared.db
  (:require [potemkin :refer [import-vars]]
            [datomic.api :as d]
            [api.shared.config :as config]))


(defn- q-maps
  "Query datomic and return results in maps
   ref: https://gist.github.com/pyrtsa/84b1c86764425a7f3def"
  [query & args]
  {:pre [(map? query)]}
  (let [ks (map #(-> % name (subs 1) keyword) (:find query))
        rs (apply datomic.api/q query args)]
    (map (partial zipmap ks) rs)))


(defn db []
  (let [conn (d/connect (:datomic-uri config/cfg))]
    (d/db conn)))


(defn q-mapped-many [query & args]
  (apply q-maps query (db) args))


(defn q-mapped [query & args]
  (first (apply q-mapped-many query args)))


(defn pull [db expr id]
  (d/pull db expr id))


(defn transact-many! [txns]
  (let [conn (d/connect (:datomic-uri config/cfg))]
    (deref (d/transact conn txns))))


(defn transact! [txn]
  (transact-many! [txn]))

