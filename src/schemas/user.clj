(ns schemas.user
  (:require [datofu.schema.dsl :refer :all]))

(def user
  [(attr   :user/id       :uuid   :identity "Unique email")
   (attr   :user/email    :string :identity "Unique email")
   (attr   :user/name     :string)
   (to-one :user/schedule :component)
   (to-one :user/goal     :component)])

(def schedule
  [(attr :schedule/days  :long   "Days dedicated to study")
   (attr :schedule/hours :string "Hours dedicated in a study day")])

(def goal
  [(attr :goal/exam   :string)
   (attr :goal/degree :string)])
