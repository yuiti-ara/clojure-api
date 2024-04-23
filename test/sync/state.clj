(ns sync.state
  (:require [clojure.test :refer :all]
            [sync.router.ops.state.differ :as diff]))


(deftest map-by-ident
  (let [datoms   [{:db/ident :x/a}
                  {:db/ident :x/b}]
        expected {:x/a {:db/ident :x/a}
                  :x/b {:db/ident :x/b}}]
    (is (= (diff/map-by-ident datoms) expected))))


(deftest missed-entities
  (let [olds_in  [{:db/ident 1} {:db/ident 2} {:db/ident 3}]
        news_in  [{:db/ident 2}]
        expected {1 {:db/ident 1} 3 {:db/ident 3}}]
    (is (= (diff/missed-entities olds_in news_in) expected))))


(defn key-set [coll]
  (->> coll keys (into #{})))


(deftest apply-checks
  (let [tuples [
                ; forbidden alterations
                [{:db/valueType   :db.type/string
                  :db/cardinality :db.cardinality/many}
                 {:db/valueType   :db.type/float
                  :db/cardinality :db.cardinality/one}
                 #{:db/valueType
                   :db/cardinality}]

                [{}
                 {:db/unique :db.unique/value}
                 #{:db/unique}]

                [{}
                 {:db/unique :db.unique/identity}
                 #{:db/unique}]

                [{}
                 {:db/isComponent true}
                 #{:db/isComponent}]

                [{:db/isComponent true}
                 {:db/isComponent false}
                 #{:db/isComponent}]

                [{:db/isComponent false}
                 {:db/isComponent true}
                 #{:db/isComponent}]

                ; allowed alterations
                [{:db/cardinality :db.cardinality/one}
                 {:db/cardinality :db.cardinality/many}
                 #{}]

                [{:db/unique :db.unique/identity} {} #{}]

                [{:db/unique :db.unique/value} {} #{}]

                ]]
    (doseq [[old_in new_in expected-keys] tuples]
      (is
        (=
          (key-set (diff/apply-checks nil old_in new_in))
          expected-keys)))))
