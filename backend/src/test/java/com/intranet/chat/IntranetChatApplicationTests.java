package com.intranet.chat;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers(disabledWithoutDocker = true)
class IntranetChatApplicationTests {

  private static final String DEMO_USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String ALICE_USER_ID = "660e8400-e29b-41d4-a716-446655440002";
  /** Not seeded; used for non-participant access checks. */
  private static final String NON_MEMBER_USER_ID = "770e8400-e29b-41d4-a716-446655440003";

  private static final String SEED_CHANNEL_ENGINEERING_ID =
      "880e8400-e29b-41d4-a716-446655440012";

  @Container
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("intranet_chat")
          .withUsername("postgres")
          .withPassword("postgres");

  @Container
  @SuppressWarnings("resource")
  static final GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add(
        "spring.r2dbc.url",
        () ->
            String.format(
                "r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(),
                postgres.getMappedPort(5432),
                postgres.getDatabaseName()));
    registry.add("spring.r2dbc.username", postgres::getUsername);
    registry.add("spring.r2dbc.password", postgres::getPassword);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
  }

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @LocalServerPort private int serverPort;

  @Test
  void contextLoads() {}

  @Test
  void actuatorHealthIsOk() {
    webTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk();
  }

  @Test
  void apiHealthIsPublic() {
    webTestClient.get().uri("/api/health").exchange().expectStatus().isOk();
  }

  @Test
  void apiRoutesRequireBearerToken() {
    webTestClient.get().uri("/api/conversations").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void loginWithValidCredentialsReturnsBearerToken() {
    webTestClient
        .post()
        .uri("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("username", "demo", "password", "password"))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.tokenType")
        .isEqualTo("Bearer")
        .jsonPath("$.accessToken")
        .exists()
        .jsonPath("$.expiresInSeconds")
        .exists();
  }

  @Test
  void loginWithInvalidPasswordReturns401() {
    webTestClient
        .post()
        .uri("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("username", "demo", "password", "wrong"))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void currentUserReturnsProfileWhenJwtValid() throws Exception {
    byte[] loginBody =
        webTestClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("username", "demo", "password", "password"))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode root = objectMapper.readTree(loginBody);
    String token = root.path("accessToken").asText();

    webTestClient
        .get()
        .uri("/api/users/me")
        .headers(h -> h.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.username")
        .isEqualTo("demo")
        .jsonPath("$.id")
        .isEqualTo(DEMO_USER_ID);
  }

  @Test
  void apiWithMockJwtCanReachSecuredRoutes() {
    webTestClient
        .mutateWith(
            mockJwt()
                .jwt(
                    j ->
                        j.subject(DEMO_USER_ID)
                            .claim("roles", List.of("USER"))))
        .get()
        .uri("/api/conversations")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray();
  }

  private String loginAccessToken(String username, String password) throws Exception {
    byte[] loginBody =
        webTestClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("username", username, "password", password))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode root = objectMapper.readTree(loginBody);
    return root.path("accessToken").asText();
  }

  @Test
  void phase3_createOrOpenDirect_returnsSameConversationId() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    Map<String, Object> body = Map.of("otherUserId", ALICE_USER_ID);

    String id1 =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(body)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    String id2 =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(body)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    Assertions.assertEquals(id1, id2);
  }

  @Test
  void phase3_sendAndListMessages_andOtherParticipantCanRead() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");

    String convId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .get()
        .uri("/api/conversations/{id}", convId)
        .headers(h -> h.setBearerAuth(aliceToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(convId);

    webTestClient
        .post()
        .uri("/api/conversations/{id}/messages", convId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of("content", "Hello from demo"))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content")
        .isEqualTo("Hello from demo");

    webTestClient
        .get()
        .uri("/api/conversations/{id}/messages?page=0&size=10", convId)
        .headers(h -> h.setBearerAuth(aliceToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.messages[0].content")
        .isEqualTo("Hello from demo")
        .jsonPath("$.totalElements")
        .isEqualTo(1);
  }

  @Test
  void phase3_unknownConversation_returns404() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    UUID missing = UUID.fromString("00000000-0000-0000-0000-000000000001");

    webTestClient
        .get()
        .uri("/api/conversations/{id}", missing)
        .headers(h -> h.setBearerAuth(demoToken))
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void phase3_nonParticipant_returns403() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String convId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .mutateWith(
            mockJwt()
                .jwt(
                    j ->
                        j.subject(NON_MEMBER_USER_ID).claim("roles", List.of("USER"))))
        .get()
        .uri("/api/conversations/{id}/messages", convId)
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void phase3_cannotOpenDirectWithSelf_returns400() throws Exception {
    String demoToken = loginAccessToken("demo", "password");

    webTestClient
        .post()
        .uri("/api/conversations/direct")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of("otherUserId", DEMO_USER_ID))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void phase3_blankMessage_returns400() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String convId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .post()
        .uri("/api/conversations/{id}/messages", convId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of("content", "   "))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void phase4_listIncludesSeededChannels() throws Exception {
    String token = loginAccessToken("demo", "password");
    byte[] body =
        webTestClient
            .get()
            .uri("/api/conversations")
            .headers(h -> h.setBearerAuth(token))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode arr = objectMapper.readTree(body);
    Assertions.assertTrue(arr.isArray());
    Assertions.assertTrue(arr.size() >= 2);
    boolean hasGeneral = false;
    boolean hasEng = false;
    for (JsonNode n : arr) {
      if ("general".equals(n.path("name").asText())
          && "CHANNEL".equals(n.path("type").asText())) {
        hasGeneral = true;
      }
      if ("engineering".equals(n.path("name").asText())
          && "CHANNEL".equals(n.path("type").asText())) {
        hasEng = true;
      }
    }
    Assertions.assertTrue(hasGeneral && hasEng);
  }

  @Test
  void phase4_channelMessageUpdatesLastPreviewAndOrdering() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String engId = SEED_CHANNEL_ENGINEERING_ID;

    webTestClient
        .post()
        .uri("/api/conversations/{id}/messages", engId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of("content", "Ship checklist approved"))
        .exchange()
        .expectStatus()
        .isOk();

    byte[] listBody =
        webTestClient
            .get()
            .uri("/api/conversations")
            .headers(h -> h.setBearerAuth(demoToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode arr = objectMapper.readTree(listBody);
    JsonNode engineering = null;
    int engIndex = -1;
    int generalIndex = -1;
    for (int i = 0; i < arr.size(); i++) {
      JsonNode n = arr.get(i);
      if (engId.equals(n.path("id").asText())) {
        engineering = n;
        engIndex = i;
      }
      if ("general".equals(n.path("name").asText())) {
        generalIndex = i;
      }
    }
    Assertions.assertNotNull(engineering);
    Assertions.assertEquals(
        "Ship checklist approved",
        engineering.path("lastMessage").path("contentPreview").asText());
    Assertions.assertTrue(generalIndex >= 0);
    Assertions.assertTrue(engIndex < generalIndex, "engineering should sort above general after activity");
  }

  @Test
  void phase4_nonMemberCannotReadChannelMessages() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");

    String soloId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/channels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("name", "solo-room"))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .get()
        .uri("/api/conversations/{id}/messages", soloId)
        .headers(h -> h.setBearerAuth(aliceToken))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void phase4_listIncludesDirectAndChannelTypes() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    webTestClient
        .post()
        .uri("/api/conversations/direct")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
        .exchange()
        .expectStatus()
        .isOk();

    byte[] body =
        webTestClient
            .get()
            .uri("/api/conversations")
            .headers(h -> h.setBearerAuth(demoToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode arr = objectMapper.readTree(body);
    boolean hasDirect = false;
    boolean hasChannel = false;
    for (JsonNode n : arr) {
      String t = n.path("type").asText();
      if ("DIRECT".equals(t)) {
        hasDirect = true;
      }
      if ("CHANNEL".equals(t)) {
        hasChannel = true;
      }
    }
    Assertions.assertTrue(hasDirect && hasChannel);
  }

  @Test
  void phase5_unreadCount_andMarkRead() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");

    String dmId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .post()
        .uri("/api/conversations/{id}/messages", dmId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(aliceToken))
        .bodyValue(Map.of("content", "Hello from alice"))
        .exchange()
        .expectStatus()
        .isOk();

    Assertions.assertEquals(
        1L,
        unreadCountForConversation(demoToken, dmId),
        "demo should see one unread message from alice");

    webTestClient
        .post()
        .uri("/api/conversations/{id}/read", dmId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(demoToken))
        .bodyValue(Map.of())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.lastReadMessageId")
        .exists();

    Assertions.assertEquals(0L, unreadCountForConversation(demoToken, dmId));
  }

  @Test
  void phase5_markReadOnlyAffectsCallingUser() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");

    String dmId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .post()
        .uri("/api/conversations/{id}/messages", dmId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(aliceToken))
        .bodyValue(Map.of("content", "Ping"))
        .exchange()
        .expectStatus()
        .isOk();

    Assertions.assertEquals(1L, unreadCountForConversation(demoToken, dmId));

    webTestClient
        .post()
        .uri("/api/conversations/{id}/read", dmId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(aliceToken))
        .bodyValue(Map.of())
        .exchange()
        .expectStatus()
        .isOk();

    Assertions.assertEquals(
        1L,
        unreadCountForConversation(demoToken, dmId),
        "alice marking read must not change demo's unread state");
  }

  @Test
  void phase5_nonParticipantCannotMarkRead() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");

    String soloId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/channels")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("name", "read-guard-test"))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    webTestClient
        .post()
        .uri("/api/conversations/{id}/read", soloId)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(h -> h.setBearerAuth(aliceToken))
        .bodyValue(Map.of())
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void phase6_presenceRestReflectsRedisAfterWebSocketConnect() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");
    String wsUrl =
        "ws://127.0.0.1:"
            + serverPort
            + "/ws?token="
            + URLEncoder.encode(demoToken, StandardCharsets.UTF_8);
    Disposable connection =
        new ReactorNettyWebSocketClient()
            .execute(URI.create(wsUrl), session -> session.receive().then())
            .subscribe();
    try {
      Thread.sleep(600);
      webTestClient
          .get()
          .uri("/api/presence/{id}", DEMO_USER_ID)
          .headers(h -> h.setBearerAuth(aliceToken))
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .jsonPath("$.status")
          .isEqualTo("ONLINE");
    } finally {
      connection.dispose();
      Thread.sleep(800);
    }
    webTestClient
        .get()
        .uri("/api/presence/{id}", DEMO_USER_ID)
        .headers(h -> h.setBearerAuth(aliceToken))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo("OFFLINE");
  }

  @Test
  void phase6_newMessageDeliveredOverWebSocket() throws Exception {
    String demoToken = loginAccessToken("demo", "password");
    String aliceToken = loginAccessToken("alice", "password");
    String dmId =
        objectMapper
            .readTree(
                webTestClient
                    .post()
                    .uri("/api/conversations/direct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> h.setBearerAuth(demoToken))
                    .bodyValue(Map.of("otherUserId", ALICE_USER_ID))
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .returnResult()
                    .getResponseBody())
            .path("id")
            .asText();

    String wsUrl =
        "ws://127.0.0.1:"
            + serverPort
            + "/ws?token="
            + URLEncoder.encode(demoToken, StandardCharsets.UTF_8);
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> payload = new AtomicReference<>();
    Disposable connection =
        new ReactorNettyWebSocketClient()
            .execute(
                URI.create(wsUrl),
                session ->
                    session
                        .receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(
                            text -> {
                              if (text.contains("MESSAGE_NEW")) {
                                payload.set(text);
                                latch.countDown();
                              }
                            })
                        .then())
            .subscribe();
    try {
      Thread.sleep(600);
      webTestClient
          .post()
          .uri("/api/conversations/{id}/messages", dmId)
          .contentType(MediaType.APPLICATION_JSON)
          .headers(h -> h.setBearerAuth(aliceToken))
          .bodyValue(Map.of("content", "Realtime payload"))
          .exchange()
          .expectStatus()
          .isOk();
      Assertions.assertTrue(latch.await(15, TimeUnit.SECONDS), "expected WS push");
      Assertions.assertTrue(payload.get().contains("MESSAGE_NEW"));
      Assertions.assertTrue(payload.get().contains("Realtime payload"));
    } finally {
      connection.dispose();
    }
  }

  private long unreadCountForConversation(String bearerToken, String conversationId)
      throws Exception {
    byte[] body =
        webTestClient
            .get()
            .uri("/api/conversations")
            .headers(h -> h.setBearerAuth(bearerToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .returnResult()
            .getResponseBody();
    JsonNode arr = objectMapper.readTree(body);
    for (JsonNode n : arr) {
      if (conversationId.equals(n.path("id").asText())) {
        return n.path("unreadCount").asLong();
      }
    }
    throw new AssertionError("conversation not in list: " + conversationId);
  }
}
