(ns schemas.core
  (:require [api.shared.loader :as loader]
            [com.rpl.specter :refer :all]))


(def datoms
  (->
    (loader/load-vars "src/schemas")
    flatten))
