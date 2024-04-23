(ns api.sessions
  (:require [clojure.test :refer :all]
            [api.mock.core :as mock]
            [api.router.core :as r]))


(deftest test-session
  (let [txs [{:db/id         "solution-id"
              :solution/id   #uuid"8c4b1aa5-4b91-47f2-877b-cf7c835b8f20"
              :solution/desc "solution desc"}

             {:db/id              "question-id"
              :question/id        #uuid"3bf14e47-da0f-4c26-95d3-a5d71ada6721"
              :question/answer    :question.alt/b,
              :question/desc      "question desc"
              :question/alts      {:question.alt/a "wrong"
                                   :question.alt/b "correct"
                                   :question.alt/c "incorrect"
                                   :question.alt/d "not right"
                                   :question.alt/e "not correct"}
              :question/solutions [{:cell/idx 0
                                    :cell/ref "solution-id"}]}
             {:session/id         #uuid"7ca26f33-2a9a-443b-bb7d-e274e11dce22"
              :session/questions  [{:cell/idx 0
                                    :cell/ref "question-id"}]
              :session/info       {:session.info/topic      :math.topic/álgebra
                                   :session.info/subtopic   :math.subtopic/logaritimo
                                   :session.info/created_at #inst"2020-01-01"}}]
        conn (mock/get-conn txs)]

    (let [deps       {:db (mock/db conn)
                      :identity {:id 1}}
          resp       (mock/get-resp r/router :get "/sessions/7ca26f33-2a9a-443b-bb7d-e274e11dce22" deps)
          expected   {:status 200
                      :body {:id        "7ca26f33-2a9a-443b-bb7d-e274e11dce22"
                             :info      {:topic      "álgebra"
                                         :subtopic   "logaritimo"
                                         :created_at "2020-01-01T00:00:00Z"}
                             :questions [{:id        "3bf14e47-da0f-4c26-95d3-a5d71ada6721"
                                          :desc      "question desc"
                                          :answer    "b"
                                          :alts      {:a "wrong"
                                                      :b "correct"
                                                      :c "incorrect"
                                                      :d "not right"
                                                      :e "not correct"}
                                          :solutions [{:id   "8c4b1aa5-4b91-47f2-877b-cf7c835b8f20"
                                                       :desc "solution desc"}]}]}}]
      (is (= resp expected)))

    (let [deps       {:db       (mock/db conn)
                      :identity {:id 1}}
          resp       (mock/get-resp r/router :get "/sessions?created_at=2020-01-01" deps)
          expected   {:status 200
                      :body [{:id        "7ca26f33-2a9a-443b-bb7d-e274e11dce22"
                              :info      {:topic      "álgebra"
                                          :subtopic   "logaritimo"
                                          :created_at "2020-01-01T00:00:00Z"}
                              :questions [{:id        "3bf14e47-da0f-4c26-95d3-a5d71ada6721"
                                           :desc      "question desc"
                                           :answer    "b"
                                           :alts      {:a "wrong"
                                                       :b "correct"
                                                       :c "incorrect"
                                                       :d "not right"
                                                       :e "not correct"}
                                           :solutions [{:id   "8c4b1aa5-4b91-47f2-877b-cf7c835b8f20"
                                                        :desc "solution desc"}]}]}]}]
      (is (= resp expected)))))
