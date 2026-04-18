# Intranet chat — backend (Spring WebFlux)

## Prerequisites

- Java 21
- Docker (for integration tests and local dependencies)

## Local dependencies (PostgreSQL + Redis)

From the **repository root** (parent of `backend/`):

```bash
docker compose up -d
```

Wait until Postgres is healthy (`docker compose ps`). Compose maps Postgres to host port **`5433`** so it does not clash with a separate PostgreSQL install on **`5432`** (a common cause of “empty” databases and missing tables). Redis stays on **`6379`**.

Defaults in `src/main/resources/application.yml` use `localhost:5433` for JDBC and R2DBC. Flyway is configured to use the **same** JDBC URL so schema migrations apply to the database the app actually uses.

Optional: copy [`backend/.env.example`](.env.example) and set variables, or export `APP_DB_*`, `APP_REDIS_*`, and `APP_JWT_*` as needed.

If your Postgres listens on **`5432`** only (no Docker), set `APP_DB_PORT=5432` and point `APP_DB_HOST` at that server.

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
- Login: `POST /api/auth/login` with JSON body `{"username":"demo","password":"password"}` (demo user is created by Flyway migration `V3__seed_demo_user.sql`; change passwords in production).
- Current user: `GET /api/users/me` with header `Authorization: Bearer <accessToken>` from login.
- Other `GET /api/**` routes require a valid JWT unless listed as public above.

## Troubleshooting

### `relation "users" does not exist` or login returns 500

The API is connected to a database where Flyway has not created the `users` table yet, or JDBC (Flyway) and R2DBC pointed at different databases.

1. Confirm you target the same Postgres as Docker Compose: **`localhost:5433`** with defaults, or set `APP_DB_*` explicitly.
2. After pulling new migrations, run a clean build so `target/classes/db/migration` is up to date: `mvn clean compile` (or `mvn clean package`).
3. Reset the Compose volume if the schema is stale: `docker compose down -v`, then `docker compose up -d`, then start the app again.
4. Check startup logs for Flyway lines (e.g. “Successfully applied migration …”).

## Build and test

```bash
cd backend
./mvnw.cmd verify
```

Integration tests in `IntranetChatApplicationTests` use Testcontainers. They run when Docker is available; if Docker is not available, that class is skipped (`@Testcontainers(disabledWithoutDocker = true)`). `JwtConfigTest` always runs and checks JWT wiring without Docker.
