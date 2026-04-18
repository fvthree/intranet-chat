package com.intranet.chat.config;

import com.intranet.chat.security.JwtProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  private static final String JWK_KID = "intranet-chat-hs256";

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

  @Bean
  JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
    byte[] secret = Base64.getDecoder().decode(jwtProperties.secret());
    OctetSequenceKey key =
        new OctetSequenceKey.Builder(secret)
            .keyID(JWK_KID)
            .algorithm(JWSAlgorithm.HS256)
            .build();
    var jwks = new ImmutableJWKSet<SecurityContext>(new JWKSet(key));
    return new NimbusJwtEncoder(jwks);
  }
}
