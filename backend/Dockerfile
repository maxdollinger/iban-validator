# Stage 1: Build frontend
FROM node:24-alpine AS frontend-build
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# Stage 2: Build backend
FROM maven:3.9-eclipse-temurin-25 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
COPY --from=frontend-build /app/dist ./src/main/resources/static/
RUN mvn package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN mkdir -p /data
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
