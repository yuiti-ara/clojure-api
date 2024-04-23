(ns api.router.sessions.core
  (:require [compojure.api.sweet :as api]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [api.router.sessions.db :as db]))


(s/defschema Solution
  {:solution/id    s/Uuid
   :solution/desc  s/Str})

(s/defschema Question
  {:question/id        s/Uuid
   :question/desc      s/Str
   :question/answer    (s/enum :question.alt/a
                               :question.alt/b
                               :question.alt/c
                               :question.alt/d
                               :question.alt/e)
   :question/alts      {:question.alt/a s/Str
                        :question.alt/b s/Str
                        :question.alt/c s/Str
                        :question.alt/d s/Str
                        :question.alt/e s/Str}
   :question/solutions [Solution]})

(s/defschema Session
  {:session/id        s/Uuid
   :session/info      {:session.info/topic      s/Keyword
                       :session.info/subtopic   s/Keyword
                       :session.info/created_at s/Inst}
   :session/questions [Question]})

(def db)
(def conn)
(def user)
(def routes [
  (api/GET "/sessions" _
    :db db
    :identity user
    :query-params [created_at :- s/Inst]
    :return [Session]
    (resp/ok (db/get-sessions Session db user created_at)))
  (api/GET "/sessions/:id" []
    :db db
    :identity user
    :path-params [id :- s/Uuid]
    :return Session
    (resp/ok (db/get-session Session db user id)))])
