(ns sync.router.ops.state.differ
  (:require [clojure.set :as set]))


(defn map-by-ident [datoms]
  (->>
    (for [datom datoms] [(:db/ident datom) datom])
    (into {})))


; missing entities


(defn- keys-set [coll]
  (-> coll keys set))

(defn missed-entities [olds news]
  (let [olds (map-by-ident olds)
        news (map-by-ident news)]
    (->>
      (set/difference (keys-set olds) (keys-set news))
      (select-keys olds))))


; entity alterations


(defn not-ok
  ([attr msg]
   (fn [_ {old attr} {new attr}]
     (if (not= old new)
       [attr {:msg msg :old old :new new}])))
  ([attr from to msg]
   (fn [_ {old attr} {new attr}]
     (if (and (= old from) (= new to))
       [attr {:msg msg :old old :new new}]))))


(def fns-check
  [(not-ok :db/valueType                                             "Types cannot be changed.")
   (not-ok :db/cardinality :db.cardinality/many :db.cardinality/one  "Cardinality many might exists.")
   (not-ok :db/unique      nil                  :db.unique/identity  "Non-unique values might exists.")
   (not-ok :db/unique      nil                  :db.unique/value     "Non-unique values might exists.")
   (not-ok :db/isComponent nil                  true                 "Non-referenced values might exists")
   (not-ok :db/isComponent false                true                 "Non-referenced values might exists")
   (not-ok :db/isComponent true                 false                "Referenced values might exists")])


(defn apply-checks [db old new]
  (->>
    (for [fn-check fns-check :let [msg (fn-check db old new)] :when (not-empty msg)] msg)
    (into {})))


(defn not-ok-alters [db olds news]
  (let [olds (map-by-ident olds)
        news (map-by-ident news)
        keys (set/intersection (keys-set olds) (keys-set news))]
    (->>
      (for [key keys
            :let [msgs (apply-checks db (key olds) (key news))]
            :when (not-empty msgs)]
        [key msgs])
      (into {}))))


; new-entities


(defn new-entities [olds news]
  (let [olds (map-by-ident olds)
        news (map-by-ident news)]
    (->>
      (for [[key new] news :when (not= new (key olds))] [key new])
      (into {}))))
