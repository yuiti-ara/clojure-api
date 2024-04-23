(ns api.router.profile.core
  (:require [compojure.api.sweet :as api]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [api.router.profile.db :as db]))


(s/defschema Profile
  {:user/name     s/Str
   :user/goal     {:goal/exam      s/Str
                   :goal/degree    s/Str}
   :user/schedule {:schedule/days  s/Int
                   :schedule/hours s/Str}})

(def db)
(def conn)
(def user)
(def routes [
  (api/GET "/profile" []
    :db db
    :identity user
    :return Profile
    (resp/ok (db/get-profile Profile db user)))
  (api/PUT "/profile" []
    :conn conn
    :identity user
    :body [profile Profile]
    :return Profile
    (resp/ok (db/put-profile Profile conn user profile)))])
