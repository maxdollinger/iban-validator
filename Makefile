.PHONY: dev dev-backend dev-frontend docker-up docker-down postgres-up postgres-down

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
