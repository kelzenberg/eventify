setup:
	cp .env.example local.env

test: api-test app-test

docker:
	docker-compose --env-file local.env up

docker-stop:
	docker-compose --env-file local.env down --remove-orphans

db-run:
	docker-compose --env-file local.env up postgres

db-reset:
	docker-compose --env-file local.env up --force-recreate --remove-orphans --build postgres

app-install:
	cd ./app && npm install

app-watch: app-install
	cd ./app && npm run build-dev && npm run watch-server

app-build: app-install
	cd ./app && npm run build-prod

app-reset:
	cd ./app && npm run clean
	$(MAKE) app-install

app-test: app-install
	cd ./app && npm test

api-clean:
	cd ./api && mvn clean:clean

api-run:
	cd ./api && ./mvnw spring-boot:run

api-test: