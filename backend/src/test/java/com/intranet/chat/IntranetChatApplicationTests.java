package com.intranet.chat;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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
}
