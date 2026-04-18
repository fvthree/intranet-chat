# Intranet chat — backend (Spring WebFlux)

Reactive REST API for authentication, user profile, direct messages, team channels, conversation listing, and per-user unread counts with mark-as-read. Persistence uses **PostgreSQL** (R2DBC + Flyway). **Redis** is on the classpath and configured for local runs, but application features do not use it yet (reserved for later phases).

## Prerequisites

- Java 21
- Docker (recommended for Postgres/Redis locally and for integration tests)

## Local dependencies (PostgreSQL + Redis)

From the **repository root** (parent of `backend/`):

```bash
docker compose up -d
```

Wait until Postgres is healthy (`docker compose ps`). Compose maps Postgres to host port **`5433`** so it does not clash with a typical PostgreSQL install on **`5432`**. Redis listens on **`6379`**.

Defaults in `src/main/resources/application.yml` use `localhost:5433` for JDBC and R2DBC. Flyway uses the **same** JDBC URL as the app so migrations always apply to the database R2DBC uses.

If your Postgres listens on **`5432`** only, set `APP_DB_PORT=5432` and point `APP_DB_HOST` at that server.

Optional: copy [`.env.example`](.env.example) under `backend/` and adjust values, or export `APP_DB_*`, `APP_REDIS_*`, and `APP_JWT_*` as needed. For non-local deployments, set **`APP_JWT_SECRET`** to a Base64-encoded 32-byte key (HS256).

## Run the API

Windows:

```bash
cd backend
./mvnw.cmd spring-boot:run
```

Unix:

```bash
cd backend
./mvnw spring-boot:run
```

Base URL: `http://localhost:8080` (override with `SERVER_PORT`).

## API overview

| Area | Auth |
|------|------|
| Health and login | Public (see below) |
| `/api/users/me`, `/api/conversations/**` | `Authorization: Bearer <accessToken>` |

### Public endpoints

- `GET /api/health` — liveness-style check for the API
- `GET /actuator/health` — Spring Boot actuator (Kubernetes-style probes)
- `POST /api/auth/login` — body `{"username":"...","password":"..."}`; returns `accessToken`, `tokenType`, `expiresInSeconds`

### Authenticated endpoints

**Current user**

- `GET /api/users/me` — profile for the JWT subject

**Conversations (direct + channels)**

- `GET /api/conversations` — all conversations the user participates in (direct and channels), ordered by latest activity (`updated_at`, then `id`). Each item includes **`unreadCount`** (messages from others not yet read, since join, excluding your own sends) and may include `lastMessage` with a short `contentPreview` when messages exist.
- `POST /api/conversations/{conversationId}/read` — optional JSON body `{"messageId":"<uuid>"}`. With no body or a null `messageId`, marks read through the **latest** non-deleted message in the conversation. Updates only the **calling user’s** read pointer (`last_read_message_id` / `last_read_at` on `conversation_participants`). Returns `lastReadMessageId` and `lastReadAt`. Non-participants receive **403**; unknown message id in this conversation returns **404**.
- `POST /api/conversations/direct` — body `{"otherUserId":"<uuid>"}` — create or return an existing 1:1 conversation (stable pair key; no duplicate pairs).
- `POST /api/conversations/channels` — body `{"name":"..."}` — create a channel; the creator is the first member.
- `GET /api/conversations/{conversationId}` — metadata if the user is a participant (`404` if missing, `403` if not a member).

**Messages (direct and channels)**

- `POST /api/conversations/{conversationId}/messages` — body `{"content":"..."}` — send a message (blank or whitespace-only content is rejected).
- `GET /api/conversations/{conversationId}/messages?page=0&size=50` — paginated history; `size` must be between 1 and 200.

Access to messages is enforced for participants only (direct chats and channels).

## Seed users and data (local development)

| Username | Password (local only) | Migration |
|----------|------------------------|-----------|
| `demo` | `password` | `V3__seed_demo_user.sql` |
| `alice` | `password` | `V5__seed_second_user.sql` |

Flyway **`V6__seed_team_channels.sql`** creates the **`general`** and **`engineering`** channels and adds **demo** and **alice** as members. Change or remove seed data in production.

## Troubleshooting

### `relation "users" does not exist` or login returns 500

The app is connected to a database where Flyway has not run, or JDBC (Flyway) and R2DBC point at different databases.

1. Use the same Postgres as Docker Compose by default: **`localhost:5433`**, or set `APP_DB_*` consistently.
2. After pulling new migrations, refresh the build output: `mvn clean compile` (or `mvn clean package`).
3. If the schema is wrong or stale, reset the Compose volume: `docker compose down -v`, then `docker compose up -d`, and start the app again.
4. Check logs for Flyway lines (for example “Successfully applied migration …”).

## Build and test

```bash
cd backend
./mvnw.cmd verify
```

On Unix, use `./mvnw verify`.

`IntranetChatApplicationTests` uses Testcontainers (Postgres and Redis). Tests run when Docker is available; if Docker is not available, that class is skipped (`@Testcontainers(disabledWithoutDocker = true)`). `JwtConfigTest` runs without Docker and covers JWT configuration.
