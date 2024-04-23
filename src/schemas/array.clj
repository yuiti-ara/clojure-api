(ns schemas.array
  (:require [datofu.schema.dsl :refer :all]
            [datofu.coll.array]))

(def array
  (datofu.coll.array/schema-tx))

(def cell
  [(attr   :cell/idx :long)
   (to-one :cell/ref :component)])
