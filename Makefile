setup-githooks:
	git config --local core.hooksPath api/ops/dev/githooks


hiera:
	lein hiera :cluster-depth 2 :vertical false :path ops/dev/ns-hierarchy.png \
		:ignore-ns api.shared \
		:ignore-ns sync.shared
	lein hiera :cluster-depth 2 :vertical false :path ops/dev/ns-hierarchy-full.png


datomic-transactor:
	envsubst < ops/dev/datomic/datomic-transactor/.credentials-template > ops/dev/datomic/datomic-transactor/.credentials
	envsubst < ops/dev/datomic/datomic-transactor/config/transactor-template.properties > ops/dev/datomic/datomic-transactor/config/transactor.properties
	docker-compose --file ops/dev/datomic/datomic-transactor/docker-compose.yml up --build -d --remove-orphans
	rm ops/dev/datomic/datomic-transactor/.credentials
	rm ops/dev/datomic/datomic-transactor/config/transactor.properties


datomic-console:
	envsubst < ops/dev/datomic/datomic-console/.credentials-template > ops/dev/datomic/datomic-console/.credentials
	docker-compose --file ops/dev/datomic/datomic-console/docker-compose.yml up --build -d --remove-orphans
	rm ops/dev/datomic/datomic-console/.credentials
	@echo "\ndatomic-console: http://localhost:9000/browse\n"


destroy-datomic:
	docker-compose --file ops/dev/datomic/datomic-transactor/docker-compose.yml down --volumes --remove-orphans
	docker-compose --file ops/dev/datomic/datomic-console/docker-compose.yml down --remove-orphans


run-datomic: datomic-transactor datomic-console


api:
	lein ring server-headless
