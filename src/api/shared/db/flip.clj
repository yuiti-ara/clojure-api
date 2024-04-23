(ns api.shared.db.flip
  (:require [clojure.walk :as walk]
            [clojure.string :as str]
            [datomic.api :as d]
            [schema.core :as s])
  (:import (clojure.lang PersistentVector PersistentArrayMap)
           (schema.core EnumSchema)))


(defn p-array-map? [coll]
  (= (type coll) PersistentArrayMap))


; filter-kvs

(declare flatten-map)

(defn- flat-kvs [coll]
  (->>
    (for [[k v] coll [ks v-new] (flatten-map v)] [(cons k ks) v-new])
    (into {})))

(defn flatten-map [coll]
  (cond
    (p-array-map? coll) (flat-kvs coll)
    (sequential? coll)  (flat-kvs (map-indexed vector coll))
    :else {[] coll}))

(defn filter-kvs [f coll]
  (->>
    (flatten-map coll)
    (map (fn [[k v]] [(last k) v]))
    (into {})
    (filter f)
    (into {})))


; map-kvs

(declare map-kvs)

(defn- map-kv [f [k v]]
  (let [[k-after v-after] (f k v)]
    (if (coll? v-after)
      [k-after (map-kvs f v-after)]
      [k-after v-after])))

(defn map-kvs [f coll]
  (cond
    (p-array-map? coll) (into {} (map #(map-kv f %) coll))
    (sequential? coll)  (into [] (map #(map-kvs f %) coll))
    :else coll))


; schema->tx

(defn- flip-id [db k v]
  (if (str/includes? k "_id")
    (let [field (str/replace (name k) "_id" "")
          k-new (keyword (namespace k) field)
          v-new (d/entid db [(keyword field "id") v])]
      [k-new v-new])
    [k v]))

(defn- add-cell [k v]
  (if (and (not= k :db/id) (= (type v) PersistentVector))
    (->>
      (into [] (map-indexed (fn [idx e] {:cell/idx idx :cell/ref e}) v))
      (vector k))
    [k v]))

(defn schema->tx [db schema]
  (->>
    schema
    (map-kvs add-cell)
    (map-kvs #(flip-id db %1 %2))))


; tx->expr

(declare remove-list)

(defn- remove-list [k v]
  (if (sequential? v)
    [k (first v)]
    [k v]))

(declare build-expr)

(defn- get-expr [[k v]]
  (if (or (p-array-map? v) (sequential? v))
    {k (build-expr v)}
    k))

(defn- build-expr [node]
  (if (p-array-map? node)
    (->>
      (map get-expr node)
      (into []))
    node))

(defn tx->expr [tx]
  (->
    (map-kvs remove-list tx)
    build-expr))


; tx->schema

(defn- unflip-id [db k v]
  (if (:db/id v)
    (let [new-k (keyword (namespace k) (str (name k) "_id"))
          attr  (keyword (name k) "id")
          tx    (d/pull db [attr] (:db/id v))]
      [new-k (attr tx)])
    [k v]))

(defn- remove-cell [v]
  (if (:cell/ref v)
    (:cell/ref v)
    v))

(defn- replace-kws [fields db k v]
  (if (contains? fields k)
    [k (d/ident db (:db/id v))]
    [k v]))

(defn- kw-keys [schema]
  (let [f #(or
             (= (type (second %)) EnumSchema)
             (= (second %) s/Keyword))]
  (->
    (filter-kvs f schema)
    keys
    set)))

(defn tx->schema [schema db tx]
  (let [fields (kw-keys schema)]
    (->>
      (map-kvs #(replace-kws fields db %1 %2) tx)
      (walk/postwalk remove-cell)
      (map-kvs #(unflip-id db %1 %2)))))
