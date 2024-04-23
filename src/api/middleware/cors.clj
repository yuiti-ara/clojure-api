(ns api.middleware.cors)


; https://gist.github.com/leordev/35bee2e7dfde38ced6b1f5236cc45c0d


(def cors-headers
  "Generic CORS headers"
  {"Access-Control-Allow-Origin"  "*"
   "Access-Control-Allow-Headers" "*"
   "Access-Control-Allow-Methods" "*"})


(defn- preflight?
  "Returns true if the request is a preflight request"
  [request]
  (= (request :request-method) :options))


(defn wrapper
  "Allow requests from all origins - also check preflight"
  [handler]
  (fn [request]
    (if (preflight? request)
      {:status 200
       :headers cors-headers
       :body "preflight complete"}
      (let [response (handler request)]
        (update-in response [:headers] merge cors-headers)))))
