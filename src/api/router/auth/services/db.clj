(ns api.router.auth.services.db
  (:require [datomic.api :as d]
            [api.shared.db :as db]))


(defn find-user [db id]
  (d/pull db [:user/id :user/name] [:user/id id]))


(defn- find-user-by-email [{email :email}]
  (let [q '{:find  [?id ?name]
            :in    [$ ?email]
            :where [[?e :user/email ?email]
                    [?e :user/id    ?id]
                    [?e :user/name  ?name]]}]
    (db/q-mapped q email)))


(defn- create-user! [uuid {:keys [name email]}]
  (let [txn {:user/id    uuid
             :user/name  name
             :user/email email}]
    (->
      (db/transact! txn)
      :db-after
      (d/pull [:user/id :user/name] [:user/email email]))))


(defn find-or-create-user [google-user uuid]
  (if-let [user (find-user-by-email google-user)]
    user
    (create-user! uuid google-user)))
