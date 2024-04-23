(ns schemas.submission
  (:require [datofu.schema.dsl :refer :all]))

(def timespan
  [(attr :timespan/started_at :instant)
   (attr :timespan/ended_at   :instant)])

(def selection
  [(to-one :submission.selection/question "Reference to a question")
   (to-one :submission.selection/selected "Reference to a question alt")
   (to-one :submission.selection/timespan :component)])

(def submission
  [(attr    :submission/id         :uuid :identity)
   (to-one  :submission/user       "Reference to a user")
   (to-one  :submission/session    "Reference to a session")
   (to-many :submission/selections :component)
   (to-one  :submission/timespan   :component)])
