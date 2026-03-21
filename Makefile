.PHONY: dev dev-backend dev-frontend docker-up docker-down postgres-up postgres-down test-backend

dev:
	make postgres-up
	make -j2 dev-backend dev-frontend

dev-backend:
	cd backend && mvn spring-boot:run

dev-frontend:
	cd frontend && npm run dev

docker-up:
	docker-compose up --build

docker-down:
	docker-compose down

postgres-up:
	docker-compose up -d postgres

postgres-down:
	docker-compose down postgres

test-backend:
	$(eval CONTAINER_ENV := $(shell \
		if command -v docker &>/dev/null && docker info &>/dev/null 2>&1; then \
			echo "docker"; \
		elif command -v podman &>/dev/null && podman info &>/dev/null 2>&1; then \
			echo "podman"; \
		else \
			echo "none"; \
		fi))
	@if [ "$(CONTAINER_ENV)" = "none" ]; then \
		echo "Error: no container runtime found. Install Docker or Podman."; \
		exit 1; \
	elif [ "$(CONTAINER_ENV)" = "podman" ]; then \
		echo "Using Podman"; \
		DOCKER_HOST=unix:///run/user/$$(id -u)/podman/podman.sock \
		TESTCONTAINERS_RYUK_DISABLED=true \
		cd backend && mvn clean test; \
	else \
		echo "Using Docker"; \
		cd backend && mvn clean test; \
	fi
