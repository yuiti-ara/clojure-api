(ns sync.router.core
  (:require [sync.router.msg :as msg]
            [sync.router.ops.core :as ops]))


(defn init [cfg]
  (->
    (ops/init (:datomic-uri cfg))
    (msg/init (:datomic-uri cfg))))


(defn run [cmd cfg code-schemas m]
  (if (= cmd :init)
    (init cfg)
    (let [{:keys [datomic-uri revision-folder revision-filename seed-filename]} cfg
          state  (ops/get-state datomic-uri code-schemas revision-folder revision-filename)]
      (case cmd
        :status (msg/status state)
        :add    (->>
                  (ops/add-new! state revision-filename)
                  (msg/add state))
        :change (->>
                  (ops/add-change! m revision-filename)
                  (msg/add state))
        :push   (->>
                  (ops/push! state datomic-uri revision-filename)
                  (msg/push state))
        :reset  (->>
                  (ops/sync-reset! datomic-uri revision-folder revision-filename)
                  (msg/sync-reset state))
        :seed   (->>
                  (ops/seed! datomic-uri seed-filename)
                  (msg/seed))))))
