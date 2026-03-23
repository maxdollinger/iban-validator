.PHONY: dev dev-backend dev-frontend stack-up stack-up-prod stack-down test-backend load-test

dev:
	make -j2 dev-backend dev-frontend

dev-backend:
	cd backend && mvn spring-boot:run

dev-frontend:
	cd frontend && npm run dev

stack-up:
	podman compose up --build -d

stack-up-prod:
	podman compose -f docker-compose.yml -f docker-compose.litestream.yml up --build -d

stack-down:
	podman compose down

test-backend:
	cd backend && mvn clean test

load-test:
	podman compose up -d --build backend
	@echo "Waiting for backend to be ready..."
	@until curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; do sleep 1; done
	@echo "Running load test..."
	wrk -t4 -c1500 -d90s 'http://localhost:8080/api/v1/iban/validation?iban=DE89370400440532013000'
	podman compose down
