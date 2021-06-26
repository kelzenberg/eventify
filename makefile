setup:
	cp .env.example local.env

docker:
	docker-compose --env-file local.env up

docker-stop:
	docker-compose --env-file local.env down --remove-orphans

db-run:
	docker-compose --env-file local.env up postgres

db-reset:
	docker-compose --env-file local.env up --force-recreate --remove-orphans --build postgres

mail:
	docker-compose --env-file local.env up mailhog

mail-reset:
	docker-compose --env-file local.env up --force-recreate --remove-orphans --build mailhog

app-install:
	cd ./app && npm install && npm run build-dev

app-watch:
	cd ./app && npm run watch-server

app-run:
	cd ./app && npm run build-dev && npm run watch-server

app-reset:
	cd ./app && npm run clean
	make app-install

api-clean:
	cd ./api && mvn clean:clean

api-run:
	cd ./api && ./mvnw spring-boot:run