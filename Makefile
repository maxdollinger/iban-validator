.PHONY: dev dev-backend dev-frontend docker-up docker-down postgres-up postgres-down test-backend

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
