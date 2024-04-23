(ns api.shared.loader
  (:require [clojure.string :as str]
            [com.rpl.specter :refer :all]))


(defn- match? [folderpath re filepath]
  (->
    (str folderpath re)
    re-pattern
    (re-matches filepath)))


(defn- importable-file? [folderpath filepath]
  (and
    (not= filepath (str folderpath "/core.clj"))
    (or
      (match? folderpath "/[a-z]*/core.clj" filepath)
      (match? folderpath "/[a-z]*.clj" filepath))))


(defn- all-filenames [folderpath]
  (->>
    (clojure.java.io/file folderpath)
    file-seq
    (map str)))


(defn- filepath->ns [filepath]
  (->
    (str/replace filepath ".clj", "")
    (str/replace "/" ".")
    (str/replace "_" "-")
    (str/replace "src." "")
    symbol))


(defn- importable-vars [filepath]
  (do
    (load-file filepath)
    (let [namespace (filepath->ns filepath)]
        (->>
          (ns-publics namespace)
          (transform [MAP-KEYS] keyword)
          (transform [MAP-VALS] var-get)
          (assoc {} (keyword namespace))))))


(defn- all-importable-vars [folderpath]
  (->>
    (all-filenames folderpath)
    (filter #(importable-file? folderpath %))
    (map importable-vars)
    (into (sorted-map))))


(defn load-vars
  ([folderpath]
   (->>
     (all-importable-vars folderpath)
     (select [MAP-VALS MAP-VALS])))
  ([folderpath var-name]
   (->>
     (all-importable-vars folderpath)
     (select [MAP-VALS var-name])
     (filter some?))))

