package com.intranet.chat.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
    String secret, String issuer, long accessTokenExpirationSeconds) {

  public JwtProperties {
    if (accessTokenExpirationSeconds <= 0) {
      throw new IllegalArgumentException("accessTokenExpirationSeconds must be positive");
    }
  }
}
