db:
	docker-compose up postgres

db-stop:
	docker-compose down --remove-orphans

db-reset:
	docker-compose up --force-recreate --remove-orphans --build postgres

app-install:
	cd ./app && npm install && npm run build-dev

app:
	cd ./app && npm run watch-server

app-reset:
	cd ./app && npm run clean
	make app-install