(ns api.router.auth.services.auth
  (:require [buddy.sign.jws :as jws]
            [buddy.sign.jwt :as jwt]
            [buddy.core.keys :as keys]
            [slingshot.slingshot :refer :all]
            [taoensso.timbre :as logger]))


(defn decode-google-token [pubkey token]
  (try+
    (jwt/unsign token (keys/str->public-key pubkey) {:alg :rs256})
    (catch [:type :validation :cause :signature] error
      (logger/info error))))

(defn decode-google-kid [token]
  (-> token jws/decode-header :kid))

(defn encode-user [user]
  {:id_token (jwt/sign user "secret")})
