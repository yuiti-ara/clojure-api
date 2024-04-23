(ns api.profile
  (:require [clojure.test :refer :all]
            [api.mock.core :as mock]
            [api.router.core :as r]))


(deftest test-profile
  (let [txs  [{:user/id    #uuid"42424242-4242-4242-4242-424242424242"
               :user/email "lisa@email.com"
               :user/name  "lisa"}]
        conn (mock/get-conn txs)
        user {:id #uuid"42424242-4242-4242-4242-424242424242"}]

    (let [deps     {:db       (mock/db conn)
                    :identity user}
          resp     (mock/get-resp r/router :get "/profile" deps)
          expected {:status 200
                    :body   {:name      "lisa"
                             :goal     {:exam   ""
                                        :degree ""}
                             :schedule {:days   0
                                        :hours "00:00"}}}]
      (is (= resp expected)))

    (let [deps    {:user     user
                   :conn     conn
                   :identity user}
          payload {:name      "lisa-new"
                   :goal     {:exam   "fuvest"
                              :degree "psicologia"}
                   :schedule {:days   7
                              :hours  "10:30"}}
          expected {:status 200
                    :body   payload}
          resp     (mock/get-resp r/router :put "/profile" deps payload)]
      (is (= resp expected)))

    (let [deps     {:db       (mock/db conn)
                    :identity user}
          resp     (mock/get-resp r/router :get "/profile" deps)
          expected {:status 200
                    :body   {:name      "lisa-new"
                             :goal     {:exam   "fuvest"
                                        :degree "psicologia"}
                             :schedule {:days   7
                                        :hours  "10:30"}}}]
      (is (= resp expected)))))
