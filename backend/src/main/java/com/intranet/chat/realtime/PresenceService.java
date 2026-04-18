package com.intranet.chat.realtime;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PresenceService {

  private static final String KEY_PREFIX = "presence:user:";
  private static final Duration TTL = Duration.ofSeconds(120);

  private final ReactiveStringRedisTemplate redis;

  public PresenceService(ReactiveStringRedisTemplate redis) {
    this.redis = redis;
  }

  private static String key(UUID userId) {
    return KEY_PREFIX + userId;
  }

  /** Marks user online with a sliding TTL (refreshed while the WebSocket stays connected). */
  public Mono<Void> touchOnline(UUID userId) {
    return redis.opsForValue().set(key(userId), "1", TTL).then();
  }

  public Mono<Void> setOffline(UUID userId) {
    return redis.delete(key(userId)).then();
  }

  public Mono<Boolean> isOnline(UUID userId) {
    return redis.hasKey(key(userId)).defaultIfEmpty(false);
  }
}
