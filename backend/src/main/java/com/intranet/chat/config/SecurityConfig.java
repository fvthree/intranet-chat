package com.intranet.chat.config;

import java.util.List;
import java.util.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
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
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
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
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOriginPatterns(
        List.of("http://localhost:*", "http://127.0.0.1:*", "http://[::1]:*"));
    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("*"));
    cors.setAllowCredentials(true);
    cors.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }

  /**
   * JWT / Bearer handling applies only to {@code /api/**}. Putting the OAuth2 resource-server
   * filters on the WebSocket upgrade ({@code GET /ws}) can break the handshake (browser shows
   * abnormal close / 1005) because the upgrade request has no {@code Authorization} header — auth
   * uses {@code ?token=} in the WebSocket handler instead.
   */
  @Bean
  @Order(0)
  SecurityWebFilterChain apiSecurityFilterChain(ServerHttpSecurity http) {
    var jwtConverter = new JwtToAuthenticationConverter();
    return http.securityMatcher(ServerWebExchangeMatchers.pathMatchers("/api", "/api/**"))
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(Customizer.withDefaults())
        .authorizeExchange(
            ex ->
                ex.pathMatchers("/api/auth/login", "/api/health")
                    .permitAll()
                    .pathMatchers("/api/**")
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
        .build();
  }

  /** Everything outside {@code /api/**} (WebSocket, actuator, etc.) — no Bearer/JWT filter. */
  @Bean
  @Order(1)
  SecurityWebFilterChain webSecurityFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(Customizer.withDefaults())
        .authorizeExchange(ex -> ex.anyExchange().permitAll())
        .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
