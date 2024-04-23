(ns sync.router.ops.core
  (:require [sync.router.ops.state.core :as state]
            [sync.router.ops.add :as add]
            [sync.router.ops.push :as push]
            [sync.router.ops.reset :as reset]
            [sync.router.ops.seed :as seed]
            [datomic.api :as d]
            [clojure.string :as str]
            [clj-time.core :as t]
            [clj-time.format :as tf]))


; conditionals

(defn may-add? [{:keys [revs entities]}]
  (and
    (= (:behind revs) 0)
    (empty? (:missing entities))
    (empty? (:not-ok entities))
    (not-empty (:new entities))))

(defn may-push? [{revs :revs}]
  (> (:behind revs) 0))

(defn may-reset? [datomic-uri]
  (clojure.string/includes? datomic-uri "dev"))


; operations

(defn get-state [datomic-uri code-schemas revision-folder revision-filename]
  (state/get-state datomic-uri code-schemas revision-folder revision-filename))


(defn add-new! [state revision-filename]
  (if (may-add? state)
    (let [news (vals (get-in state [:entities :new]))]
      (add/add-revision! news revision-filename))))


(defn- deprec-name [ident]
  (let [dt (tf/unparse (tf/formatter "|yyyy-MM-dd|HH:mm:ss|z") (t/now))]
    (->
      (str ident)
      (str/replace ":" "")
      (str/replace "/" ".")
      (str dt)
      (->> (keyword "deprecated")))))


(defn- deprec-tx [ident]
  {:db/id    ident
   :db/ident (deprec-name ident)})


(defn- rename-tx [[old-ident new-ident]]
  {:db/id    old-ident
   :db/ident new-ident})


(defn- component-tx [[ident is-comp]]
  {:db/ident       ident
   :db/isComponent is-comp})


(defn- build-txs [[kw ident-map]]
  (let [f (kw {:component component-tx
               :rename    rename-tx
               :deprec    deprec-tx})]
    (map f ident-map)))


(defn add-change! [m revision-filename]
  (add/add-revision! (flatten (map build-txs m)) revision-filename))


(defn push! [state datomic-uri revision-filename]
  (if (may-push? state)
    (push/push! datomic-uri revision-filename)))


(defn sync-reset! [datomic-uri revision-folder revision-filename]
  (when (may-reset? datomic-uri)
    (reset/reset-all! datomic-uri revision-folder revision-filename)
    (push/push! datomic-uri revision-filename)))


(defn init [datomic-uri]
  (d/create-database datomic-uri))


(defn seed! [datomic-uri seed-filename]
  (seed/seed! datomic-uri seed-filename))
