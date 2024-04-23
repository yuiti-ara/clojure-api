(ns api.shared.coercer
  (:import (clojure.lang PersistentArrayMap)
           (schema.core EnumSchema)))


; add prefix

(defn- namespace-map [ks]
  (->>
    (for [k ks] [(-> k name keyword) (-> k namespace keyword)])
    (into {})))

(defn- apply-prefix [prefix-map k]
  (keyword (name prefix-map) (name k)))

(defn- new-keys-map [prefix-map ks]
  (->>
    (for [k ks :when (prefix-map k)] [k (apply-prefix (prefix-map k) k)])
    (into {})))

(defn- rename-enum [prefix-map [k v]]
  (if (prefix-map k)
    [k (apply-prefix (prefix-map k) v)]
    [k v]))

(defn- enum-map [schema]
  (->>
    (for [[k v] schema :when (= (type v) EnumSchema)] [k (keyword (namespace (first (:vs v))))])
    (into {})))

(defn add-prefix [schema]
  (if (= (type schema) PersistentArrayMap)
    (fn [data]
      (let [mapper-ns       (namespace-map (keys schema))
            mapper-enums    (enum-map schema)
            mapper-new-keys (new-keys-map mapper-ns (keys data))]
        (->>
          (clojure.set/rename-keys data mapper-new-keys)
          (map #(rename-enum mapper-enums %))
          (into {}))))))

(defn wrap-add-prefix [f]
  (fn [schema]
    (let [f1 (f schema)
          f2 (add-prefix schema)]
      (fn [data]
        (let [d1 (if (fn? f1) (f1 data) data)
              d2 (if (fn? f2) (f2 d1) d1)]
          d2)))))


; remove prefix

(defn- remove-prefix [v]
  (if (keyword? v)
    (keyword (name v))
    v))

(defn- coerce-body [resp]
  (->>
    (:body resp)
    (clojure.walk/postwalk remove-prefix)
    (assoc resp :body)))

(defn wrap-remove-prefix [router]
  (fn [request]
    (->
      request
      router
      coerce-body)))
