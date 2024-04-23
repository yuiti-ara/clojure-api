(ns api.router.auth.services.core
  (:require [api.router.auth.services.auth :as auth]
            [api.router.auth.services.http :as http]
            [api.router.auth.services.db :as db]
            [slingshot.slingshot :refer :all]))

(defn throw-if-not [arg func error-msg]
  (if-let [resp (func arg)]
    resp
    (throw+ {:msg error-msg})))

(defn post-login [{code :authorization_code} uuid]
  (let [token (http/request-google-token code)]
    (->
      token
      auth/decode-google-kid
      http/request-google-pubkey
      (throw-if-not #(auth/decode-google-token % token) :unauthorized)
      (db/find-or-create-user uuid)
      auth/encode-user)))

(defn get-user [db {id :id}]
  (db/find-user db id))
