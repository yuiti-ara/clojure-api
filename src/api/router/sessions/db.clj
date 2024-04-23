(ns api.router.sessions.db
  (:require [api.shared.db.core :as db]
            [datomic.api :as d]))


(defn get-session [schema db {user-id :id} session-id]
  (db/pull schema db [:session/id session-id]))


(defn- get-session-ids [db]
  (let [query '{:find  [(take 3 ?e)]
                :where [[?e :session/id _]]}]
    (->>
      (d/q query db)
      flatten)))


(defn get-sessions [schema db {user-id :id} created_at]
  (let [ids (get-session-ids db)]
    (db/pull-many schema db ids)))
