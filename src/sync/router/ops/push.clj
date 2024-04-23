(ns sync.router.ops.push
  (:require [sync.shared.io :as io]
            [datomic.api :as d]
            [io.rkn.conformity :as c]))


(defn push! [datomic-uri filename]
  (let [conn      (d/connect datomic-uri)
        revisions (io/read-resource filename)]
    (c/ensure-conforms conn revisions)
    true))
