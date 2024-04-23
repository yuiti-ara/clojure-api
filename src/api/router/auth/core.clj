(ns api.router.auth.core
  (:require [compojure.api.sweet :as api]
            [ring.util.http-response :as resp]
            [schema.core :as s]
            [slingshot.slingshot :refer :all]
            [api.router.auth.services.core :as services]))


(s/defschema ReqAuthCode
  {:authorization_code s/Str})

(s/defschema RespToken
  {:id_token s/Str})

(s/defschema User
  {:user/id   s/Uuid
   :user/name s/Str})

(def uuid)
(def user)
(def db)
(def routes [
  (api/GET "/auth/user" []
    :db db
    :identity user
    :return User
    (resp/ok (services/get-user db user)))
  (api/POST "/auth/login" []
    :uuid uuid
    :body [payload ReqAuthCode]
    :return RespToken
    (try+
      (resp/ok (services/post-login payload uuid))
      (catch [:msg :unauthorized] error
        (resp/unauthorized error))))])
