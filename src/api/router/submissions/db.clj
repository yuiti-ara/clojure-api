(ns api.router.submissions.db
  (:require [api.shared.db.core :as db]))


(defn get-submission [schema db {id :id} submission_id]
  (db/pull schema db [:submission/id submission_id]))


(defn post-submission [schema conn {id :id} submission uuid]
  (->
    submission
    (assoc :submission/user_id id)
    (assoc :submission/id uuid)
    (->> (db/insert! schema conn))
    (dissoc :submission/user_id)))
