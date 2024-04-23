(ns api.middleware.logger.setup
  (:require [clojure.string :as str]
            [clojure.core.strint :refer [<<]]
            [clojure.term.colors :as c]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [api.shared.config :as config])
  (:import (com.fasterxml.jackson.core JsonGenerationException)))


(defn- if-debug [msg color-fn]
  (if (:api-debug config/cfg)
    (color-fn msg)
    msg))

(defn- format-map [msg]
  (try
    (as->
      msg $
      (json/generate-string $ {:pretty true})
      (str "\n" $ "\n"))
    (catch JsonGenerationException _ msg)))

(defn- format-kw [msg]
  (->
    (str msg " ")
    (if-debug c/magenta)))

(defn- format-msg [msg]
  (cond
    (map? msg)     (format-map msg)
    (keyword? msg) (format-kw msg)
    :else (str msg " ")))

(defn- format-msgs [msgs]
  (->
    (for [msg msgs] (format-msg msg))
    str/join
    (str/replace "\n\n" "\n")
    str/trim-newline))

(defn- output-fn [{:keys [timestamp_ ?ns-str ?line level vargs]}]
  (let [timestamp (as-> timestamp_ $ (force $) (<< "[~{$}]") (if-debug $ c/magenta))
        level     (-> level (str/replace ":" "") (if-debug c/blue))
        file-line (-> (<< "[~{?ns-str}:~{?line}]") (if-debug c/blue))
        msg       (-> vargs force format-msgs)]
    (<< "~{timestamp} ~{level} ~{file-line} ~{msg}")))

(defn setup-logger! []
  (timbre/merge-config! {:output-fn output-fn}))
