(ns sync.router.ops.state.db-schemas
  (:require [datomic.api :as d]
            [taoensso.timbre :as logger]))


(def query-schemas
  '[:find ?e ?ident
    :where
    [?e :db/ident ?ident]
    [(namespace ?ident) ?ns]
    (not (or [(contains? #{"db" "fressian" "conformity" "deprecated"} ?ns)]
             [(clojure.string/starts-with? ?ns "db.")]))])


(defn find-db-schemas [db]
  (->>
    (d/q query-schemas db)
    (map #(->> % first (d/entity db) d/touch (into {})))
    (into [])))


(defn find-revision-ids [db]
  (let [query '{:find  [?revs]
                :where [[_ :conformity/conformed-norms ?revs]]}]
    (try
      (d/q query db)
      (catch Exception _
        (do
          (logger/info "No revisions found in db")
          [])))))
