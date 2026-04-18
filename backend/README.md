# Intranet chat — backend (Spring WebFlux)

## Prerequisites

- Java 21
- Docker (for integration tests and local dependencies)

## Local dependencies (PostgreSQL + Redis)

From the **repository root** (parent of `backend/`):

```bash
docker compose up -d
```

This starts PostgreSQL on port `5432` and Redis on `6379` with credentials matching the defaults in `src/main/resources/application.yml`.

Optional: copy [`backend/.env.example`](.env.example) and set variables, or export `APP_DB_*`, `APP_REDIS_*`, and `APP_JWT_SECRET` as needed.

## Run the API

```bash
cd backend
./mvnw.cmd spring-boot:run
```

On Unix:

```bash
cd backend
./mvnw spring-boot:run
```

- Application: `http://localhost:8080`
- Public health: `GET /api/health`
- Actuator health (Kubernetes-style probes): `GET /actuator/health`
- Secured API example: `GET /api/**` (except documented public paths) requires a valid JWT (Bearer token). Login and token issuance are **Phase 2**.

## Build and test

```bash
cd backend
./mvnw.cmd verify
```

Integration tests in `IntranetChatApplicationTests` use Testcontainers. They run when Docker is available; if Docker is not available, that class is skipped (`@Testcontainers(disabledWithoutDocker = true)`). `JwtConfigTest` always runs and checks JWT wiring without Docker.
