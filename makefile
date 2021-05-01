db:
	docker-compose up postgres

db-stop:
	docker-compose down --remove-orphans

db-reset:
	docker-compose up --force-recreate --remove-orphans --build postgres