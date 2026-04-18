package com.intranet.chat.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(JwtConfig.class)
@TestPropertySource(
    properties =
        "app.security.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
class JwtConfigTest {

  @Autowired private ReactiveJwtDecoder reactiveJwtDecoder;

  @Test
  void reactiveJwtDecoderBeanIsCreated() {
    assertThat(reactiveJwtDecoder).isNotNull();
  }
}
