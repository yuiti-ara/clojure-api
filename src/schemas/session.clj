(ns schemas.session
  (:require [datofu.schema.dsl :refer :all]))

(def math
  [(named :math.topic/álgebra)
   (named :math.subtopic/aritmética)
   (named :math.subtopic/potência)
   (named :math.subtopic/logaritimo)
   (named :math.subtopic/fatoração)
   (named :math.subtopic/raiz-quadrada)
   (named :math.subtopic/inequação)
   (named :math.subtopic/módulo)
   (named :math.subtopic/número-complexo)
   (named :math.subtopic/equação-do-2º-grau)])

(def solution
  [(attr   :solution/id   :db.type/uuid :identity)
   (to-one :solution/desc :string)])

(def alts
  [(attr  :question.alt/a :string)
   (attr  :question.alt/b :string)
   (attr  :question.alt/c :string)
   (attr  :question.alt/d :string)
   (attr  :question.alt/e :string)
   (named :question.alt/skipped)])

(def question
  [(attr    :question/id        :db.type/uuid :identity)
   (to-one  :question/desc      :string)
   (to-one  :question/alts      :component)
   (to-one  :question/answer    :component)
   (to-many :question/solutions :component)])

(def info
  [(to-one :session.info/topic)
   (to-one :session.info/subtopic)
   (attr   :session.info/created_at :instant)])

(def session
  [(attr    :session/id        :db.type/uuid :identity)
   (to-one  :session/info      :component)
   (to-many :session/questions :component)])
