package com.intranet.chat.config;

import com.intranet.chat.security.JwtProperties;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  @Bean
  ReactiveJwtDecoder reactiveJwtDecoder(JwtProperties jwtProperties) {
    byte[] secret = Base64.getDecoder().decode(jwtProperties.secret());
    if (secret.length < 32) {
      throw new IllegalStateException(
          "app.security.jwt.secret must be Base64 and decode to at least 32 bytes (HS256)");
    }
    var key = new SecretKeySpec(secret, "HmacSHA256");
    return NimbusReactiveJwtDecoder.withSecretKey(key).build();
  }
}
