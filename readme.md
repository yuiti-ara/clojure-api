
## clojure api

#### api dependency graph
![](ops/dev/ns-hierarchy.png)
#### api dependency graph (including shared ns)
![](ops/dev/ns-hierarchy-full.png)

#### setup githooks

    make setup-githooks    

#### run the api

    # start server
    lein ring server

#### run local datomic instance

    # start datomic
    make run-datomic

    # destroy datomic
    make destroy-datomic
