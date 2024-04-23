(ns api.router.auth.services.http
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [slingshot.slingshot :refer [throw+ try+]]
            [api.shared.config :as config]
            [taoensso.timbre :as logger])
  (:import (clojure.lang ExceptionInfo)))


(defn- log-error [error]
  (->
    error
    Throwable->map
    :data
    :body
    logger/info))

(defn- send-request [body url]
  (->
    (try
      (http/post url {:content-type :json :body (json/generate-string body)})
      (catch ExceptionInfo error
        (do
          (log-error error)
          (throw+ {:type :unauthorized}))))
    :body
    (json/parse-string true)))

(defn request-google-token [code]
  (->
    {:code          code
     :client_id     (:google-oauth-client-id config/cfg)
     :client_secret (:google-oauth-client-secret config/cfg)
     :redirect_uri  (:google-oauth-redirect-uri config/cfg)
     :grant_type    "authorization_code"}
    (send-request "https://oauth2.googleapis.com/token")
    :id_token))

(defn request-google-pubkey [key_id]
  (let [key (keyword key_id)]
    (->
      (http/get "https://www.googleapis.com/oauth2/v1/certs")
      :body
      (json/parse-string true)
      key)))
