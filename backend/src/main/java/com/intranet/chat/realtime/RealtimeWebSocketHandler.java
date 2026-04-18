package com.intranet.chat.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RealtimeWebSocketHandler implements WebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(RealtimeWebSocketHandler.class);

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
    log.info("WebSocket handler sessionId={} uri={}", session.getId(), session.getHandshakeInfo().getUri());
    String token = extractAccessToken(session);
    if (token == null) {
      log.warn("WebSocket closed: missing token query param");
      return session.close(
          CloseStatus.BAD_DATA.withReason("Missing token (use ?token=<jwt> or ?access_token=<jwt>)"));
    }
    return jwtDecoder
        .decode(token)
        .flatMap(jwt -> connect(UUID.fromString(jwt.getSubject()), session))
        .onErrorResume(
            ex -> {
              log.warn("WebSocket error: {}", ex.toString(), ex);
              return session.close(
                  CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token or session error"));
            });
  }

  private Mono<Void> connect(UUID userId, WebSocketSession session) {
    log.info("WebSocket authenticated userId={} sessionId={}", userId, session.getId());
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
    // One subscription to session.receive() only. Mono.when + takeUntilOther(inbound) would
    // subscribe twice to a cold Mono and trigger Reactor Netty "Rejecting additional inbound receiver".
    Mono<Void> inboundDone = session.receive().then().share();
    Mono<Void> heartbeat =
        Flux.interval(Duration.ofSeconds(45))
            .concatMap(
                tick ->
                    presenceService.touchOnline(userId).onErrorResume(ex -> Mono.empty()))
            .takeUntilOther(inboundDone)
            .then();
    return Mono.when(inboundDone, heartbeat)
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
