(ns api.router.submissions.core
  (:require [compojure.api.sweet :as api]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [api.router.submissions.db :as db]))


(s/defschema Timespan
  #:timespan{:started_at s/Inst
             :ended_at   s/Inst})

(s/defschema Submission
  #:submission{:id         s/Uuid
               :session_id s/Uuid
               :timespan   Timespan
               :selections [#:submission.selection{:question_id s/Uuid
                                                   :selected    (s/enum :question.alt/a
                                                                        :question.alt/b
                                                                        :question.alt/c
                                                                        :question.alt/d
                                                                        :question.alt/e
                                                                        :question.alt/skipped)
                                                   :timespan    Timespan}]})


(def db)
(def conn)
(def user)
(def uuid)
(def routes [
  (api/GET "/submissions/:id" _
    :db db
    :identity user
    :path-params [id :- s/Uuid]
    :return Submission
    (resp/ok (db/get-submission Submission db user id)))
  (api/POST "/submissions" _
    :uuid uuid
    :conn conn
    :identity user
    :body [submission (dissoc Submission :submission/id)]
    :return Submission
    (resp/ok (db/post-submission Submission conn user submission uuid)))])
