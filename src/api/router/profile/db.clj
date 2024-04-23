(ns api.router.profile.db
  (:require [api.shared.db.core :as db]))


(defn get-profile [schema db {user_id :id}]
  (let [defaults {:user/goal     {:goal/exam      ""
                                  :goal/degree    ""}
                  :user/schedule {:schedule/days  0
                                  :schedule/hours "00:00"}}]
    (->>
      (db/pull schema db [:user/id user_id])
      (merge defaults))))


(defn put-profile [schema conn {user_id :id} profile]
  (->
    (db/insert! schema conn profile [:user/id user_id])
    (dissoc :db/id)))
