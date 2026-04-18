# API endpoints — curl examples

Assumptions:

- API base URL: `http://localhost:8080` (override host/port if needed).
- Seed users for local dev: **`demo`** / **`password`**, **`alice`** / **`password`** (see Flyway migrations).
- Fixed UUIDs used in examples:
  - **demo**: `550e8400-e29b-41d4-a716-446655440001`
  - **alice**: `660e8400-e29b-41d4-a716-446655440002`

Set a shell variable for the access token after login:

```bash
export BASE=http://localhost:8080
export TOKEN="<paste accessToken from login response>"
```

---

## Public (no JWT)

### `GET /api/health`

```bash
curl -sS "$BASE/api/health"
```

### `POST /api/auth/login`

```bash
curl -sS -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}'
```

Example with `jq` to capture the token (if `jq` is installed):

```bash
export TOKEN="$(curl -sS -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}' | jq -r .accessToken)"
echo "$TOKEN"
```

---

## Authenticated (`Authorization: Bearer <token>`)

All requests below need:

```bash
-H "Authorization: Bearer $TOKEN"
```

### `GET /api/users/me`

```bash
curl -sS "$BASE/api/users/me" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Conversations

### `GET /api/conversations`

List conversations for the current user (includes `unreadCount`, optional `lastMessage`).

```bash
curl -sS "$BASE/api/conversations" \
  -H "Authorization: Bearer $TOKEN"
```

### `POST /api/conversations/direct`

Open or create a 1:1 conversation with another user.

```bash
curl -sS -X POST "$BASE/api/conversations/direct" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"otherUserId":"660e8400-e29b-41d4-a716-446655440002"}'
```

### `POST /api/conversations/channels`

Create a channel; the creator becomes the first member.

```bash
curl -sS -X POST "$BASE/api/conversations/channels" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"my-team-channel"}'
```

### `GET /api/conversations/{conversationId}`

```bash
export CONV_ID="<conversation-uuid>"
curl -sS "$BASE/api/conversations/$CONV_ID" \
  -H "Authorization: Bearer $TOKEN"
```

### `POST /api/conversations/{conversationId}/read`

Mark read up to the latest message (empty body), or up to a specific message.

```bash
curl -sS -X POST "$BASE/api/conversations/$CONV_ID/read" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}'
```

Optional body with a specific message id:

```bash
curl -sS -X POST "$BASE/api/conversations/$CONV_ID/read" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"messageId":"00000000-0000-0000-0000-000000000000"}'
```

(Replace the UUID with a real message id from history.)

---

## Messages

### `POST /api/conversations/{conversationId}/messages`

```bash
curl -sS -X POST "$BASE/api/conversations/$CONV_ID/messages" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from curl"}'
```

### `GET /api/conversations/{conversationId}/messages`

Paginated history (`page` default `0`, `size` default `50`, max `200`).

```bash
curl -sS "$BASE/api/conversations/$CONV_ID/messages?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Presence

### `GET /api/presence/{userId}`

```bash
curl -sS "$BASE/api/presence/550e8400-e29b-41d4-a716-446655440001" \
  -H "Authorization: Bearer $TOKEN"
```

Returns JSON like `{"userId":"...","status":"ONLINE"}` or `"OFFLINE"`.

---

## WebSocket (not curl)

**`GET /ws?token=<jwt>`** (or `access_token=<jwt>`) — browsers cannot send `Authorization` on the WebSocket handshake, so the access token is passed as a query parameter.

`curl` does **not** speak the WebSocket protocol. Use one of:

- **[websocat](https://github.com/vi/websocat)**  
  `websocat "ws://localhost:8080/ws?token=$TOKEN"`
- **Browser** devtools / a small HTML page using `new WebSocket(...)`
- **Postman** or **Insomnia** WebSocket request

Example URL (URL-encode the token if it contains special characters):

```text
ws://localhost:8080/ws?token=<accessToken>
```

After connect, other participants may receive JSON events such as `PRESENCE` (online/offline) and `MESSAGE_NEW` when someone posts a message.

---

## Actuator (often used without JWT in local setups — check your security config)

### `GET /actuator/health`

```bash
curl -sS "$BASE/actuator/health"
```

---

## Quick scripted flow (bash)

```bash
BASE=http://localhost:8080
TOKEN="$(curl -sS -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}' | jq -r .accessToken)"

curl -sS "$BASE/api/users/me" -H "Authorization: Bearer $TOKEN"

CONV="$(curl -sS -X POST "$BASE/api/conversations/direct" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"otherUserId":"660e8400-e29b-41d4-a716-446655440002"}' | jq -r .id)"

curl -sS -X POST "$BASE/api/conversations/$CONV/messages" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"hi"}'

curl -sS "$BASE/api/conversations" -H "Authorization: Bearer $TOKEN"
```

---

## Windows (PowerShell) note

Use `curl.exe` explicitly if `curl` is aliased to `Invoke-WebRequest`, or use:

```powershell
$base = "http://localhost:8080"
$body = '{"username":"demo","password":"password"}'
$r = Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType "application/json" -Body $body
$token = $r.accessToken
Invoke-RestMethod -Uri "$base/api/users/me" -Headers @{ Authorization = "Bearer $token" }
```
