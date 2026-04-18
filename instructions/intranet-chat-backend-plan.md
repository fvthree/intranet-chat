# Intranet Chat Backend — Phase Plan

## 1. Project Overview

This project is an **internal team messaging backend** built with **Spring Boot WebFlux** as a **modular monolith**.

The goal is to create a practical intranet chat backend that supports real-time messaging, conversation management, unread tracking, and user presence, while keeping the architecture simple enough for a small team or solo developer to build and maintain.

This version will focus on the **Core v1 features** only.

## 2. Chosen Product Direction

### Product Type
Internal team messaging backend for intranet use.

### Architecture Style
**Modular monolith**

This means the application is deployed as a single service, but the codebase is organized into clear modules so it remains maintainable and can evolve later if needed.

### Core v1 Features
- user login
- direct messaging
- team channels
- send message
- list conversations
- fetch message history
- unread message count
- mark as read
- online/offline presence
- WebSocket real-time delivery

## 3. Recommended Technology Stack

The recommended stack for this project is:

- **Spring Boot WebFlux**
- **Spring Security (Reactive)**
- **PostgreSQL** using **R2DBC**
- **Redis** for presence, lightweight caching, and real-time support
- **WebSocket** for live chat delivery
- **JWT** for authentication

## 4. Why This Stack Fits

This recommendation is a strong fit because:

- **WebFlux** is suitable for chat systems with many concurrent connections.
- **WebSocket** provides real-time delivery for messages and presence updates.
- **PostgreSQL** gives a solid relational structure for conversations, messages, and read tracking.
- **R2DBC** keeps the application reactive end-to-end.
- **Redis** helps track presence and can support lightweight pub/sub style event handling.
- A **modular monolith** keeps development simpler than full microservices while still encouraging clean boundaries.

## 5. High-Level Module Design

The modular monolith can be organized into these internal modules:

### 5.1 Auth Module
Responsibilities:
- login
- JWT issuance and validation
- authentication context
- role and permission checks

### 5.2 User Module
Responsibilities:
- user profile lookup
- employee directory basics
- presence state
- user search

### 5.3 Conversation Module
Responsibilities:
- direct conversations
- team channels
- conversation membership
- conversation listing

### 5.4 Message Module
Responsibilities:
- send message
- fetch message history
- mark messages as read
- unread count calculation

### 5.5 Realtime Module
Responsibilities:
- WebSocket connection handling
- real-time message delivery
- presence broadcast

## 6. Core v1 Scope Definition

The first version will include only the following capabilities:

### Authentication
- users can log in
- authenticated users receive a JWT
- secured endpoints require authentication

### Direct Messaging
- a user can create or open a 1:1 conversation
- a user can send messages in that conversation
- a user can fetch message history

### Team Channels
- users can view channels they belong to
- users can send messages to those channels
- users can fetch channel history

### Conversation Listing
- users can list their available direct conversations and channels
- latest activity should help determine ordering

### Unread Tracking
- users can see unread message counts
- users can mark a conversation as read

### Presence
- users can appear online or offline
- presence should be visible to other authenticated users where applicable

### Realtime Delivery
- connected users receive new messages in real time through WebSocket
- presence changes can be pushed in real time

## 7. Suggested Data Model

These are the main entities for v1.

### User
Fields:
- id
- employee_id
- username
- display_name
- email
- department
- role
- active
- created_at
- updated_at

### Conversation
Fields:
- id
- type (`DIRECT`, `CHANNEL`)
- name
- created_by
- created_at
- updated_at

### ConversationParticipant
Fields:
- id
- conversation_id
- user_id
- joined_at
- last_read_message_id
- last_read_at

### Message
Fields:
- id
- conversation_id
- sender_id
- content
- created_at
- updated_at
- deleted

### Presence
Fields:
- user_id
- status (`ONLINE`, `OFFLINE`)
- last_seen_at

## 8. API Surface Outline

### REST Endpoints
Authentication:
- `POST /api/auth/login`

Users:
- `GET /api/users/me`
- `GET /api/users/search`

Conversations:
- `GET /api/conversations`
- `POST /api/conversations/direct`
- `GET /api/conversations/{conversationId}`
- `GET /api/conversations/{conversationId}/messages`
- `POST /api/conversations/{conversationId}/messages`
- `POST /api/conversations/{conversationId}/read`
- `GET /api/conversations/{conversationId}/unread-count`

Channels:
- `GET /api/channels`
- `GET /api/channels/{channelId}/messages`
- `POST /api/channels/{channelId}/messages`

Presence:
- `GET /api/presence/{userId}`

### WebSocket Events
Incoming or outgoing event ideas:
- `SEND_MESSAGE`
- `NEW_MESSAGE`
- `MESSAGE_READ`
- `USER_ONLINE`
- `USER_OFFLINE`

## 9. Delivery Phases

Below is a suggested phased plan for the **Core v1 features**.

---

## Phase 1 — Foundation and Project Setup

### Goal
Prepare the modular monolith foundation and the technical skeleton of the application.

### Features Included
- project setup
- module boundaries
- reactive security baseline
- database setup
- Redis setup
- shared error handling and response model

### Tasks
- create Spring Boot WebFlux project
- configure PostgreSQL with R2DBC
- configure Redis
- define package/module structure
- create base security configuration
- define JWT strategy
- set up migrations
- create common exception handling
- create health and readiness endpoints
- prepare local Docker Compose for dependencies

### Acceptance Criteria
- application starts successfully
- database connection works
- Redis connection works
- module structure is in place
- secured and public routes are clearly separated
- project can run locally through a repeatable setup

---

## Phase 2 — Authentication and User Access

### Goal
Enable secure login and authenticated access for intranet users.

### Features Included
- user login
- JWT issuance
- authenticated access
- current user profile endpoint

### Tasks
- design user table and user lookup flow
- implement login endpoint
- generate JWT on successful login
- validate JWT for protected routes
- expose current authenticated user endpoint
- add role and authority extraction
- add authentication tests

### Acceptance Criteria
- valid users can log in successfully
- invalid credentials are rejected
- protected endpoints require a valid JWT
- authenticated user identity is available in request handling
- current user profile endpoint works correctly

---

## Phase 3 — Direct Messaging

### Goal
Allow users to create and use 1:1 conversations.

### Features Included
- direct messaging
- send message
- fetch message history

### Tasks
- create direct conversation model
- prevent duplicate 1:1 conversations where appropriate
- implement create/open direct conversation endpoint
- implement send message endpoint
- implement paginated message history endpoint
- store sender and timestamp metadata
- add validation for empty or invalid messages
- add tests for conversation and message flows

### Acceptance Criteria
- a user can create or open a direct conversation with another user
- a user can send messages successfully
- message history can be retrieved in correct order
- only conversation participants can access that conversation
- invalid message content is rejected properly

---

## Phase 4 — Team Channels and Conversation Listing

### Goal
Support team channel messaging and let users see all their available conversations.

### Features Included
- team channels
- list conversations

### Tasks
- design channel-backed conversation model
- implement channel membership rules
- implement channel message send endpoint
- implement channel message history endpoint
- implement conversation listing endpoint
- include latest message summary and latest activity time
- support conversation ordering by recent activity
- add tests for membership and listing behavior

### Acceptance Criteria
- a user can view channels they belong to
- a user can send messages to allowed channels
- a user can fetch channel message history
- conversation listing includes both direct conversations and channels
- conversation listing is ordered by latest activity
- users cannot access channels they are not part of

---

## Phase 5 — Unread Tracking and Read Status

### Goal
Provide a usable chat experience by tracking unread messages and read state.

### Features Included
- unread message count
- mark as read

### Tasks
- define read tracking strategy using participant state
- implement unread count calculation
- implement mark conversation as read endpoint
- update participant read markers
- return unread counts in conversation listing
- add tests for unread transitions

### Acceptance Criteria
- unread counts are available per conversation
- unread counts decrease correctly after marking as read
- read markers are stored consistently
- users only affect their own read state
- conversation listing reflects unread status accurately

---

## Phase 6 — Presence and Realtime Delivery

### Goal
Enable real-time messaging and presence updates using WebSocket.

### Features Included
- online/offline presence
- WebSocket real-time delivery

### Tasks
- implement WebSocket configuration
- authenticate WebSocket sessions
- map connected users to active sessions
- publish new messages to connected recipients in real time
- publish online/offline presence changes
- store lightweight presence state in Redis
- handle connect and disconnect lifecycle
- add integration tests for real-time events where practical

### Acceptance Criteria
- authenticated users can connect through WebSocket
- new messages are delivered in real time to connected users
- online and offline state updates correctly
- disconnected users are marked offline after session close or timeout
- presence state is available for lookup when needed

---

## 10. Suggested Build Order Summary

Recommended implementation order:

1. foundation and setup
2. authentication
3. direct messaging
4. team channels and conversation listing
5. unread tracking
6. presence and real-time delivery

This order allows the system to grow from a secure backend foundation into a usable chat product without introducing real-time complexity too early.

## 11. Notes for Future Versions

These are intentionally out of scope for Core v1, but good candidates for later phases:

- typing indicators
- message edit and delete
- reactions
- attachments
- message search
- pinned messages
- audit logs
- admin moderation
- retention policies
- LDAP / Active Directory / SSO integration

## 12. Final Recommendation

The recommended implementation is:

**Spring Boot WebFlux + PostgreSQL (R2DBC) + Redis + WebSocket + JWT**

Built as a **modular monolith** with the following Core v1 phased delivery:

- Phase 1 — Foundation and project setup
- Phase 2 — Authentication and user access
- Phase 3 — Direct messaging
- Phase 4 — Team channels and conversation listing
- Phase 5 — Unread tracking and read status
- Phase 6 — Presence and real-time delivery

This is a practical and scalable learning path for building an intranet messaging backend with reactive Spring.
