(defproject api "0.1.0"
  :repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                    :username [:env/datomic_user]
                                    :password [:env/datomic_pass]}]]
  :dependencies [[buddy/buddy-auth "2.2.0"]
                 [cheshire "5.10.0"]
                 [clj-http "3.10.0"]
                 [cli-matic "0.3.11"]
                 [clojure-term-colors "0.1.0"]
                 [clojure.java-time "0.3.2"]
                 [com.datomic/datomic-pro "0.9.6045"]
                 [com.jakemccrary/lein-test-refresh "0.24.1"]
                 [com.rpl/specter "1.1.3"]
                 [com.vodori/schema-conformer "0.1.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [danlentz/clj-uuid "0.1.9"]
                 [io.rkn/conformity "0.5.4"]
                 [medley "1.3.0"]
                 [metosin/compojure-api "2.0.0-alpha31"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/core.incubator "0.1.4"]
                 [org.clojure/tools.reader "1.2.2"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [potemkin "0.4.5"]
                 [prone "2020-01-17"]
                 [ring/ring-devel "1.8.0"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-mock "0.4.0"]
                 [selmer "1.12.18"]
                 [slingshot "0.12.2"]
                 [vvvvalvalval/datofu "0.1.0"]
                 [yogthos/config "1.1.7"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-hiera "1.1.0"]
            [lein-eftest "0.5.9"]]
  :repl-options {:port 8081}
  :ring {:handler api.core/handler
         :port 5000
         :auto-reload? true}
  :aliases {"sync" ["run" "-m" "sync.core"]}
  :main api.core
  :min-lein-version "2.0.0"
  :uberjar-name "api.jar"
  :profiles {:dev {:ring {:stacktrace-middleware prone.middleware/wrap-exceptions}}
             :uberjar {:aot :all}})
