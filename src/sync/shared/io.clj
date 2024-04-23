(ns sync.shared.io
  (:require [clojure.core.strint :refer [<<]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.java.shell :as shell]
            [clojure.pprint :as pp]
            [clojure.string :as str]))


(defn read-resource [filename]
  (->
    filename
    io/resource
    io/file
    slurp
    edn/read-string))


(defn read-revisions-on-master [folderpath filename]
  (->
    (shell/sh "git" "show" (str "master:" folderpath "/" filename))
    :out
    edn/read-string))


(defn- stringfy [revisions]
  (->
    (pp/pprint revisions)
    (with-out-str)
    (str/replace "}" "}\n")
    (str/replace "\n," ",\n")
    (str/replace "}\n)]}" "})]}")
    (str/replace-first "{" "{\n ")
    (str/replace "}\n}" "}}")))


(defn ident-first [datom]
  (merge {:db/ident (:db/ident datom)} datom))


(defn sorted-datoms [datoms]
  (sort-by :db/ident (map ident-first datoms)))


(defn sorted-revision [{[datoms & _] :txes [requires] :requires}]
  (let [requires (if requires {:requires [requires]})
        txes     {:txes [(sorted-datoms datoms)]}]
    (merge requires txes)))


(defn sorted-within-revs [revisions]
  (->>
    (for [[id revision] revisions] [id (sorted-revision revision)])
    (into {})))


(defn- sorted-between-revs [coll]
  (into (sorted-map-by #(compare %2 %1)) coll))


(defn pretty-str [revisions]
  (->
    revisions
    sorted-between-revs
    sorted-within-revs
    stringfy))


(defn replace-file [revisions filename]
  (do
    (if-let [file (io/resource filename)]
      (io/delete-file file))
    (spit (<< "resources/~{filename}") revisions)))


(defn replace-revision-file [revisions filename]
  (->
    (pretty-str revisions)
    (replace-file filename)))
