(ns api.submissions
  (:require [clojure.test :refer :all]
            [api.mock.core :as mock]
            [api.router.core :as r]))


(deftest test-submission
  (let [txs     [{:user/id     #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dda19"}
                 {:session/id  #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dda20"}
                 {:question/id #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dda21"}]
        deps    {:identity {:id #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dda19"}
                 :conn     (mock/get-conn txs)
                 :uuid     #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dda20"}
        payload {:id         "7ca26f33-2a9a-443b-bb7d-e274e11dda20"
                 :session_id "7ca26f33-2a9a-443b-bb7d-e274e11dda20"
                 :timespan   {:started_at   "1970-01-01T00:00:00Z"
                              :ended_at     "1970-01-01T00:00:10Z"}
                 :selections [{:question_id "7ca26f33-2a9a-443b-bb7d-e274e11dda21"
                               :selected    "b"
                               :timespan    {:started_at "1970-01-01T00:00:00Z"
                                             :ended_at   "1970-01-01T00:00:10Z"}}]}]
    (let [resp     (mock/get-resp r/router :post "/submissions" deps (dissoc payload :id))
          expected {:status 200
                    :body   payload}]
      (is (= resp expected)))

    (let [db       (mock/db (:conn deps))
          deps     (assoc deps :db db)
          resp     (mock/get-resp r/router :get "/submissions/7ca26f33-2a9a-443b-bb7d-e274e11dda20" deps)
          expected {:status 200
                    :body   payload}]
      (is (= resp expected)))))
