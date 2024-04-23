(ns api.middleware.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as auth]
            [buddy.auth.accessrules :as rules]
            [taoensso.timbre :as logger])
  (:import (java.util UUID)))


(defn authfn [identity]
  (let [id       (UUID/fromString (:id identity))
        identity (assoc identity :id id)]
    (logger/info :auth identity)
    identity))


(def backend
  (let [opts {:token-name "Bearer"
              :secret "secret"
              :authfn authfn}]
    (backends/jws opts)))


(defn- rule-free [_]
  (rules/success))


(defn- rule-auth [request]
  (if (:identity request)
    (rules/success)
    (rules/error "Not authorized")))


(def access-rules
  {:rules [{:uris    ["/auth/login", "/"]
            :handler rule-free}
           {:pattern #"^/.*"
            :handler rule-auth}]})


(defn wrapper [router]
  (->
    router
    (rules/wrap-access-rules access-rules)
    (auth/wrap-authentication backend)))
