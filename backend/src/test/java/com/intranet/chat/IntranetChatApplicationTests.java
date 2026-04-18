package com.intranet.chat;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
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
        .isNotFound();
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
}
