.PHONY: build up down logs restart db-reset clean-logs clean-all help

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
		rm -f logs/app.log; \
	else \
		echo "Aborted! Database was NOT deleted."; \
	fi

clean-logs:
	rm -f logs/app.log

clean-all:
	rm -rf logs

# Show help for all make commands
help:
	@echo "Available make commands:"
	@echo "  build       - Build the Java project (skip tests)"
	@echo "  up          - Build and start all Docker Compose services"
	@echo "  down        - Stop all Docker Compose services"
	@echo "  logs        - Follow logs from all Docker Compose services"
	@echo "  restart     - Restart all Docker Compose services"
	@echo "  db-reset    - Remove all containers, networks, db_data volume, and logs/app.log (DANGER: deletes all MySQL data)"
	@echo "  clean-logs  - Delete logs/app.log only"
	@echo "  clean-all   - Delete the entire logs directory and all logs" 