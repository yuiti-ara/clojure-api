(ns sync.core
  (:require [cli-matic.core :refer [run-cmd]]
            [sync.router.core :as router]
            [api.shared.config :as config]
            [schemas.core :as code-schemas]))

(defn run
  ([cmd] (run cmd {}))
  ([cmd m]
    (router/run cmd config/cfg code-schemas/datoms m)))


(def cli-config
  {:app       {:command     "sync"
               :description "Migration tool for datomic schemas"
               :version     "0.0.1"}

   :commands [{:command     "status"
               :description ["Show sync status"]
               :runs        (fn [_] (if (some? (run :status)) 0 1))}

              {:command     "add"
               :description ["Add new revision to revision file"]
               :runs        (fn [_] (run :add))}

              {:command     "change"
               :description ["Apply schema changes"]
               :opts        [{:short "m" :option "map" :type :edn}]
               :runs        (fn [{m :map}] (run :change m))}

              {:command     "push"
               :description ["Push file revisions to db"]
               :runs        (fn [_] (run :push))}

              {:command     "reset"
               :description ["Hard reset on revision-file & db-instance"]
               :runs        (fn [_] (run :reset))}

              {:command     "seed"
               :description ["Push seed data"]
               :runs        (fn [_] (run :seed))}

              {:command     "init"
               :description ["Create db"]
               :runs        (fn [_] (run :init))}]})


(defn -main [& args]
  (run-cmd *command-line-args* cli-config))
