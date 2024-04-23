(ns api.shared.config
  (:require [config.core :refer [env]]
            [slingshot.slingshot :refer [throw+]]
            [schema.core :as s]
            [schema.utils :as su]
            [schema-conformer.core :as sc]))


(s/defschema Config
  {:api-debug                  (sc/default s/Bool false)
   :port                       (sc/default s/Int 5000)
   :datomic-uri                (sc/default s/Str "datomic:dev://localhost:4334/api-db")
   :revision-filename          (sc/default s/Str "revisions.edn")
   :revision-folder            (sc/default s/Str "api/resources")
   :seed-filename              (sc/default s/Str "seed.edn")
   :google-oauth-client-id     s/Str
   :google-oauth-client-secret s/Str
   :google-oauth-redirect-uri  s/Str})


(defn- load-config [env schema]
  (select-keys env (keys schema)))


(defonce cfg
  (let [resp (sc/conform Config (load-config env Config))]
    (if (su/error? resp)
      (throw+ {:type :config-error :cause (:error resp)})
      resp)))
