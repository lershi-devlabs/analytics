.PHONY: build up down logs restart db-reset

build:
	./mvnw clean package -DskipTests

up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f

restart: down up

# Stop and remove all containers, networks, and the db_data volume for a fresh database
# WARNING: This will delete all MySQL data!
db-reset:
	@read -sp "Are you sure you want to delete ALL MySQL data? Type 'yes' to continue: " ans; \
	echo ""; \
	if [ "$$ans" = "yes" ]; then \
		docker compose down -v; \
	else \
		echo "Aborted! Database was NOT deleted."; \
	fi 