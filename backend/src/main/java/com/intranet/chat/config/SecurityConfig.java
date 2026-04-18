package com.intranet.chat.config;

import java.util.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  /**
   * Not a Spring bean: {@code Converter} beans are registered with WebFlux {@code
   * FormattingConversionService}, which cannot infer generic types from lambda-based converters.
   */
  private static final class JwtToAuthenticationConverter
      implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();

    JwtToAuthenticationConverter() {
      authorities.setAuthoritiesClaimName("roles");
      authorities.setAuthorityPrefix("ROLE_");
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
      Collection<GrantedAuthority> auths = authorities.convert(jwt);
      return Mono.just(new JwtAuthenticationToken(jwt, auths));
    }
  }

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    var jwtConverter = new JwtToAuthenticationConverter();
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            ex -> ex.pathMatchers(
                    "/actuator/health",
                    "/actuator/health/**",
                    "/actuator/info",
                    "/api/health",
                    "/api/auth/login",
                    "/ws")
                .permitAll()
                .pathMatchers("/api/**")
                .authenticated()
                .anyExchange()
                .denyAll())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
        .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
