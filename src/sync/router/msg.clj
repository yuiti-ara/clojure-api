(ns sync.router.msg
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [selmer.parser :as parser]))


(defn pretty-keys [datoms]
  (->>
    datoms
    keys
    sort
    (str/join "\n    ")))


(defn entities-name-lists [entities]
  (->>
    (for [[kw datoms] entities] [kw (pretty-keys datoms)])
    (into {})))


(defn print-msg [entities revs db-dev? filename]
  (let [entities (entities-name-lists entities)
        db-dev   {:db-dev? db-dev?}
        msg-vars (merge entities revs db-dev)]
    (parser/set-resource-path! (io/resource "sync-templates"))
    (->
      (str filename ".html")
      (parser/render-file msg-vars)
      println)))


(defn status [{:keys [entities revs db-dev?]}]
  (cond
    (> (:behind revs) 0)               (print-msg entities revs db-dev? "status-behind")
    (or
      (not-empty (:not-ok entities))
      (not-empty (:missing entities))) (print-msg entities revs db-dev? "status-forbidden")
    (not-empty (:new entities))        (print-msg entities revs db-dev? "status-ready-to-add")
    :else                              (do (print-msg entities revs db-dev? "status-synced") true)))


(defn add [{:keys [entities revs db-dev?]} success?]
  (let  [not-allowed (some not-empty [(:missing entities) (:not-ok entities)])]
    (cond
      success?                              (print-msg entities revs db-dev? "add-success")
      (or not-allowed (> (:behind revs) 0)) (print-msg entities revs db-dev? "add-not-allowed")
      (empty? (:new entities))              (print-msg entities revs db-dev? "add-not-needed"))))


(defn push [{:keys [entities revs db-dev?]} success?]
  (cond
    success?             (print-msg entities revs db-dev? "push-success")
    (= (:behind revs) 0) (print-msg entities revs db-dev? "push-not-needed")
    :else                (println "push not performed")))


(defn sync-reset [{:keys [entities revs db-dev?]} success?]
  (if success?
    (print-msg entities revs db-dev? "reset-success")
    (print-msg entities revs db-dev? "reset-not-allowed")))


(defn init [success? datomic-uri]
  (if success?
    (println "DB created succesfully\n")
    (println "DB already exists\n")))


(defn seed [success?]
  (if success?
    (println "Seed pushed successfully!\n")
    (println "No seed data to push\n")))
