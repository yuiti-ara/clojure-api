(ns api.router.test.core
  (:require [compojure.api.sweet :as api]
            [ring.util.http-response :as resp]
            [schema.core :as s])
  (:import (java.util Date)))


(s/defschema Test
  {:a/x s/Int
   :b/y Date})


(def routes [
  (api/POST "/test" _
    :body [payload Test]
    :return Test
    (resp/ok payload))])
