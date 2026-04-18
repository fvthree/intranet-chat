package com.intranet.chat.realtime;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RealtimeConnectionRegistry {

  private final Map<UUID, CopyOnWriteArraySet<WebSocketSession>> userSessions =
      new ConcurrentHashMap<>();

  public void add(UUID userId, WebSocketSession session) {
    userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
  }

  public void remove(UUID userId, WebSocketSession session) {
    CopyOnWriteArraySet<WebSocketSession> set = userSessions.get(userId);
    if (set == null) {
      return;
    }
    set.remove(session);
    if (set.isEmpty()) {
      userSessions.remove(userId);
    }
  }

  public boolean hasSessions(UUID userId) {
    CopyOnWriteArraySet<WebSocketSession> set = userSessions.get(userId);
    return set != null && !set.isEmpty();
  }

  public Set<UUID> connectedUserIds() {
    return Set.copyOf(userSessions.keySet());
  }

  public Mono<Void> sendToUser(UUID userId, String jsonPayload) {
    CopyOnWriteArraySet<WebSocketSession> set = userSessions.get(userId);
    if (set == null || set.isEmpty()) {
      return Mono.empty().then();
    }
    return Flux.fromIterable(set)
        .flatMap(
            session ->
                session
                    .send(Flux.just(session.textMessage(jsonPayload)))
                    .onErrorResume(
                        err -> {
                          remove(userId, session);
                          return Mono.empty();
                        }))
        .then();
  }

  /** Fan-out to every connected user except {@code excludeUserId} (optional). */
  public Mono<Void> broadcastExcept(UUID excludeUserId, String jsonPayload) {
    return Flux.fromIterable(userSessions.entrySet())
        .filter(e -> excludeUserId == null || !e.getKey().equals(excludeUserId))
        .flatMap(e -> sendToUser(e.getKey(), jsonPayload))
        .then();
  }
}
