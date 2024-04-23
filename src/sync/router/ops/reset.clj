(ns sync.router.ops.reset
  (:require [datomic.api :as d]
            [sync.shared.io :as io]))


(defn reset-all! [datomic-uri revision-folder filename]
  (do
    (d/delete-database datomic-uri)
    (d/create-database datomic-uri)
    (->
      (io/read-revisions-on-master revision-folder filename)
      (io/replace-revision-file filename))
    true))
