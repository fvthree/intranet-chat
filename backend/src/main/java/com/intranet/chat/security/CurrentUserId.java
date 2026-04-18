package com.intranet.chat.security;

import java.util.UUID;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CurrentUserId {

  public Mono<UUID> get() {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> (Jwt) ctx.getAuthentication().getPrincipal())
        .map(Jwt::getSubject)
        .map(UUID::fromString);
  }
}
