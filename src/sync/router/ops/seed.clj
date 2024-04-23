(ns sync.router.ops.seed
  (:require [datomic.api :as d]
            [sync.shared.io :as io]))


(defn seed! [datomic-uri seed-filename]
  (d/transact (d/connect datomic-uri) (io/read-resource seed-filename)))
