(ns api.mock.db
  (:require [datomic.api :as d]
            [schemas.core :as schemas]))


(defn get-conn
  ([] (get-conn nil))
  ([txs]
   (let [uri "datomic:mem://api-db"]
     (d/delete-database uri)
     (d/create-database uri)
     (let [conn (d/connect uri)]
       @(d/transact conn schemas/datoms)
       (when (some? txs)
         @(d/transact conn txs))
       conn))))


(defn db
  ([conn]
   (d/db conn))
  ([conn txs]
   (let [db (d/db conn)]
     (:db-after (d/with db txs)))))
