(ns sync.router.ops.add
  (:require [clojure.string :as str]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [sync.shared.io :as io]))


(defn- new-rev-name [now]
  (->
    (f/unparse (f/formatter "yyyy-MM-dd|HH:mm:ss|z") now)
    str/lower-case
    (str/replace " " "-")
    keyword))


(defn- new-revision [datoms prev-name]
  (let [requires (if prev-name {:requires [prev-name]})
        txes     {:txes [datoms]}
        new-name (new-rev-name (time/now))]
    {new-name (merge requires txes)}))


(defn add-revision! [new-datoms filename]
  (let [revisions (io/read-resource filename)
        prev-name (-> revisions keys sort last)]
    (->
      (new-revision new-datoms prev-name)
      (merge revisions)
      (io/replace-revision-file filename))
    true))
