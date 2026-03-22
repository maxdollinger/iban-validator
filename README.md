# IBAN Validation Service

Validates IBANs by checking pattern, checksum, and (where supported) bank + account number.
Currently implements German (DE) IBAN validation with bank lookup via PostgreSQL.

## Architecture

```
frontend/          React 19 SPA (Vite, TanStack Router + Query, Tailwind)
backend/           Spring Boot 4 REST API (WebFlux, Spring Data JPA, Java 25)
terraform/         Infrastructure-as-code
docker-compose.yml Full stack with Podman
```

The frontend calls `GET /api/v1/iban/validation?iban=...` on the backend.
The backend parses the IBAN, looks up the bank in PostgreSQL, and returns validation results.

## Prerequisites

- [Nix](https://nixos.org/download/) with flakes enabled
- [Podman](https://podman.io/) (used instead of Docker)

## Getting Started

```bash
# Enter the dev environment (provides JDK 25, Node.js 24, Podman, Maven)
nix develop

# Start PostgreSQL + backend + frontend in one command
make dev
```

Backend runs on `http://localhost:8080`, frontend on `http://localhost:5173`.

## Useful Commands

| Command              | Description                                    |
|----------------------|------------------------------------------------|
| `make dev`           | Start everything (postgres + backend + frontend) |
| `make dev-backend`   | Start only the Spring Boot backend             |
| `make dev-frontend`  | Start only the Vite frontend                   |
| `make test-backend`  | Run backend tests (uses Testcontainers + Podman) |
| `make postgres-up`   | Start only PostgreSQL                          |
| `make postgres-down` | Stop PostgreSQL                                |
| `make docker-up`     | Start full stack via Podman Compose             |
| `make docker-down`   | Stop full stack                                |

## API

### Validate IBAN

```
GET /api/v1/iban/validation?iban=DE89370400440532013000
```

```json
{
  "iban": "DE89370400440532013000",
  "patternValid": true,
  "bankName": "Commerzbank",
  "accountNumberValidation": "NOT_IMPLEMENTED",
  "error": null
}
```

### Upsert Bank

```
POST /api/v1/bank/
Content-Type: application/json

{
  "bankCode": "37040044",
  "countryCode": "DE",
  "name": "Commerzbank",
  "accountAlgo": null
}
```
