package com.intranet.chat.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RealtimeWebSocketHandler implements WebSocketHandler {

  private final ReactiveJwtDecoder jwtDecoder;
  private final PresenceService presenceService;
  private final RealtimeConnectionRegistry registry;
  private final ObjectMapper objectMapper;

  public RealtimeWebSocketHandler(
      ReactiveJwtDecoder jwtDecoder,
      PresenceService presenceService,
      RealtimeConnectionRegistry registry,
      ObjectMapper objectMapper) {
    this.jwtDecoder = jwtDecoder;
    this.presenceService = presenceService;
    this.registry = registry;
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    String token = extractAccessToken(session);
    if (token == null) {
      return session.close(
          CloseStatus.BAD_DATA.withReason("Missing token (use ?token=<jwt> or ?access_token=<jwt>)"));
    }
    return jwtDecoder
        .decode(token)
        .flatMap(jwt -> connect(UUID.fromString(jwt.getSubject()), session))
        .onErrorResume(
            ex ->
                session.close(
                    CloseStatus.NOT_ACCEPTABLE.withReason("Invalid or expired token")));
  }

  private Mono<Void> connect(UUID userId, WebSocketSession session) {
    registry.add(userId, session);
    return presencePayload(userId, "ONLINE")
        .flatMap(
            json ->
                presenceService
                    .touchOnline(userId)
                    .onErrorResume(ex -> Mono.empty())
                    .then(registry.broadcastExcept(userId, json).onErrorResume(ex -> Mono.empty())))
        .then(runSession(userId, session));
  }

  private Mono<Void> runSession(UUID userId, WebSocketSession session) {
    Mono<Void> inbound = session.receive().then();
    Mono<Void> heartbeat =
        Flux.interval(Duration.ofSeconds(45))
            .concatMap(
                tick ->
                    presenceService.touchOnline(userId).onErrorResume(ex -> Mono.empty()))
            .takeUntilOther(inbound)
            .then();
    return Mono.when(inbound, heartbeat)
        .then(Mono.defer(() -> onSessionClosed(userId, session)));
  }

  private Mono<Void> onSessionClosed(UUID userId, WebSocketSession session) {
    registry.remove(userId, session);
    if (registry.hasSessions(userId)) {
      return Mono.empty();
    }
    return presencePayload(userId, "OFFLINE")
        .flatMap(
            json ->
                presenceService
                    .setOffline(userId)
                    .onErrorResume(ex -> Mono.empty())
                    .then(registry.broadcastExcept(userId, json).onErrorResume(ex -> Mono.empty())));
  }

  private Mono<String> presencePayload(UUID userId, String status) {
    return Mono.fromCallable(
        () -> {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("type", "PRESENCE");
          m.put("userId", userId.toString());
          m.put("status", status);
          return objectMapper.writeValueAsString(m);
        });
  }

  private static String extractAccessToken(WebSocketSession session) {
    var uri = session.getHandshakeInfo().getUri();
    String q = uri.getQuery();
    if (q == null || q.isEmpty()) {
      return null;
    }
    for (String part : q.split("&")) {
      int eq = part.indexOf('=');
      if (eq <= 0) {
        continue;
      }
      String name = URLDecoder.decode(part.substring(0, eq), StandardCharsets.UTF_8);
      if ("token".equals(name) || "access_token".equals(name)) {
        return URLDecoder.decode(part.substring(eq + 1), StandardCharsets.UTF_8);
      }
    }
    return null;
  }
}
