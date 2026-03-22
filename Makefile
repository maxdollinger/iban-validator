.PHONY: dev dev-backend dev-frontend docker-up docker-down postgres-up postgres-down test-backend load-test

dev:
	make postgres-up
	make -j2 dev-backend dev-frontend

dev-backend:
	cd backend && mvn spring-boot:run

dev-frontend:
	cd frontend && npm run dev

docker-up:
	podman compose up --build

docker-down:
	podman compose up down

postgres-up:
	podman compose up -d postgres

postgres-down:
	podman compose down postgres

test-backend:
	DOCKER_HOST=unix:///run/user/$$(id -u)/podman/podman.sock \
	TESTCONTAINERS_RYUK_DISABLED=true \
	cd backend && mvn clean test; \

load-test:
	podman compose up -d --build postgres backend
	@echo "Waiting for backend to be ready..."
	@until curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; do sleep 1; done
	@echo "Running load test..."
	wrk -t4 -c1500 -d90s 'http://localhost:8080/api/v1/iban/validation?iban=DE89370400440532013000'
	podman compose down postgres backend
