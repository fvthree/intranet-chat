# Intranet Chat Frontend (React) — Phase Plan

## 1. Project Overview

This document describes a **phased plan for a React frontend** that talks to the **intranet-chat** Spring WebFlux API (JWT auth, REST conversations/messages, read state, presence, WebSocket realtime).

The goal is to grow the UI from a minimal shell into a usable intranet chat client without blocking on features the backend does not yet expose.

This plan aligns loosely with the backend phases: **auth → conversations → messages → realtime → polish**.

---

## 2. Recommended Technology Stack

| Area | Suggestion |
|------|------------|
| UI | **React** (18+) with **TypeScript** |
| Routing | **React Router** |
| Build | **Vite** (or Create React App / similar) |
| HTTP | `fetch` or **axios**; central API module with base URL from env |
| State | **React Query (TanStack Query)** for server state (optional but helpful) |
| WebSocket | Native **`WebSocket`** API, one connection per app (or per tab) |
| Styling | CSS modules, Tailwind, or your team standard |

Environment variable examples:

- `VITE_API_BASE_URL` (Vite) or `REACT_APP_API_BASE_URL` → e.g. `http://localhost:8080`

---

## 3. Backend API Mapping (reference)

Use these alongside [`api-endpoints-curl.md`](api-endpoints-curl.md) and the backend README.

| Backend capability | Frontend phases (rough) |
|--------------------|---------------------------|
| Login, `/api/users/me` | Phase F2, F3 |
| Conversation list, open DM, create channel | Phase F4, F5 |
| Messages + mark read | Phase F6 |
| WebSocket `/ws`, presence REST | Phase F7, F8 |

---

## Phase F1 — App shell, routing, API client

### Goal

A runnable React app with routing, env-based API base URL, and an HTTP helper that can attach `Authorization: Bearer <token>` when present.

### Features / tasks

- React Router (or equivalent) with a simple layout (e.g. header + main).
- Read API base URL from environment (`import.meta.env` / `process.env`).
- Small `apiClient` module: `GET`/`POST` JSON, attach bearer token from app state or storage.
- Basic global handling for **401** (e.g. clear token and redirect to login).

### Acceptance criteria

- Navigating between placeholder routes works.
- With a token set manually (or from F2), `GET /api/users/me` succeeds against a running backend.

### Implemented in this repo (Next.js App Router)

- Env: `NEXT_PUBLIC_API_BASE_URL` (see `frontend/.env.example`).
- API helpers: `frontend/src/lib/intranet-api/` (`env.ts`, `token.ts`, `client.ts`, `types.ts`).
- Routes: `/intranet`, `/intranet/login` (placeholder), `/intranet/dev` (paste JWT → call `/api/users/me`).
- Template home (`/`) links to `/intranet`.

---

## Phase F2 — Authentication UI

### Goal

Login wired to `POST /api/auth/login`, token persistence, and protected routes.

### Features / tasks

- Login form: username + password.
- On success, store `accessToken` (and optionally parse `expiresInSeconds` if you add refresh logic later).
- Logout clears token and in-memory user state.
- Protected route wrapper: no valid token → redirect to `/login`.

### Acceptance criteria

- Valid credentials load the authenticated shell; `GET /api/users/me` matches the logged-in user.
- Invalid credentials show an error and do not persist a token.

---

## Phase F3 — Current user & profile strip

### Goal

Show who is logged in using `GET /api/users/me`.

### Features / tasks

- Header (or sidebar) shows username / display name from the API response.
- Optional placeholder route for “Settings”.

### Acceptance criteria

- After login, profile information is visible and matches the API.

---

## Phase F4 — Conversation list

### Goal

Primary screen: list conversations from `GET /api/conversations`.

### Features / tasks

- Fetch list on load; handle loading / empty / error states.
- Render each row: title (DM vs channel), ordering follows API (`updated_at`).
- Show **`unreadCount`** and **`lastMessage`** preview when returned.
- Click row → navigate to thread route, e.g. `/conversations/:conversationId`.

### Acceptance criteria

- Seeded channels (e.g. `general`, `engineering`) and any DMs appear when the backend data exists.
- Unread counts from the API are visible in the list.

---

## Phase F5 — Open / create conversations

### Goal

Create DMs and channels from the UI.

### Features / tasks

- **New DM**: choose another user (static list, hardcoded seed UUIDs for dev, or a future users API) → `POST /api/conversations/direct` with `otherUserId`.
- **New channel**: form with name → `POST /api/conversations/channels`.
- After success, refresh the list or navigate to the new/existing thread.

### Acceptance criteria

- Opening an existing DM is idempotent (same conversation id returned).
- New channels appear in the list and can be opened.

---

## Phase F6 — Message thread (REST)

### Goal

Read and send messages for one conversation using REST.

### Features / tasks

- `GET /api/conversations/{id}/messages?page=&size=` — paginate or “load older”.
- `POST /api/conversations/{id}/messages` — send text; reject empty content client-side to match backend.
- Composer at bottom; scroll behavior for new messages.
- Optional: `POST /api/conversations/{id}/read` when the user has viewed latest (e.g. on focus or when scrolled to bottom).

### Acceptance criteria

- Works for both DMs and channels the user belongs to.
- After marking read (if implemented), `unreadCount` on the list decreases on next refresh.

---

## Phase F7 — Realtime (WebSocket)

### Goal

Live message updates without full-page refresh.

### Features / tasks

- Open **one** WebSocket to `ws://<host>/ws?token=<accessToken>` (or `access_token=`); URL-encode the token if needed.
- Handle JSON events:
  - **`MESSAGE_NEW`**: merge into active thread if `conversationId` matches; optionally update list previews.
  - **`PRESENCE`**: optional UI updates (see F8).
- Reconnect strategy: on close, backoff retry; on 401 from REST, force re-login.
- Token in query string is required because browsers do not send `Authorization` on the WebSocket handshake.

### Acceptance criteria

- Two browsers (or two users) see new messages appear without manual refresh.
- Disconnecting does not permanently break the app after reconnect.

---

## Phase F8 — Presence & UX polish

### Goal

Surface online/offline state and tighten the experience.

### Features / tasks

- `GET /api/presence/{userId}` for indicators next to names (call when rendering a user or batch if you add a bulk API later).
- Responsive layout; keyboard and focus basics.
- Consistent error toasts or inline errors.

### Acceptance criteria

- Presence matches “has an active WS + Redis TTL” vs “offline” in simple manual tests.
- Main flows work on smaller viewports.

**Note:** Typing indicators and granular read receipts are **not** part of the Core v1 backend plan unless you extend the API.

---

## Phase F9 — Hardening (optional)

### Goal

Production readiness and maintainability.

### Features / tasks

- E2E tests (Playwright / Cypress): login, list, send message.
- Document CORS and WebSocket URL for deployed API (`wss://`, correct host).
- i18n / themes if required by the org.

### Acceptance criteria

- CI can run E2E against a test stack; README documents env vars for staging/production.

---

## 10. Suggested Build Order Summary

1. **F1** — Shell + API client  
2. **F2** — Login + protected routes  
3. **F3** — Profile header  
4. **F4** — Conversation list  
5. **F5** — Create DM / channel  
6. **F6** — Message thread (REST)  
7. **F7** — WebSocket realtime  
8. **F8** — Presence + polish  
9. **F9** — Hardening (as needed)  

You can merge **F3** into **F2** or **F4** if you want fewer phases; the critical sequence is **auth → list → thread → realtime → polish**.
