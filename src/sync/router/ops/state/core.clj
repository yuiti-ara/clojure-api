(ns sync.router.ops.state.core
  (:require [clojure.string :as str]
            [clojure.core.strint :refer [<<]]
            [datomic.api :as d]
            [sync.router.ops.state.differ :as diff]
            [sync.router.ops.state.db-schemas :as db-schemas]
            [sync.shared.io :as io]))


(defn get-state [datomic-uri code-schemas revision-folder filename]
  (let [db           (d/db (d/connect datomic-uri))
        file-count   (-> (io/read-resource filename) keys count)
        master-count (-> (io/read-revisions-on-master revision-folder filename) keys count)
        db-count     (-> (db-schemas/find-revision-ids db) count)
        db-schemas   (-> (db-schemas/find-db-schemas db))]
    {:db-dev?            (str/includes? datomic-uri "dev")
     :revs     {:behind  (- file-count db-count)
                :added   (- file-count master-count)
                :pushed  (max (- db-count master-count) 0)}
     :entities {:missing    (diff/missed-entities db-schemas code-schemas)
                :not-ok     (diff/not-ok-alters db db-schemas code-schemas)
                :new        (diff/new-entities db-schemas code-schemas)}}))
