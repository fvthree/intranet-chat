package com.intranet.chat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Logs every HTTP request to {@code /ws} (including the upgrade). If you see 1005 in the browser
 * but nothing here, the handshake is not reaching this JVM (wrong port, firewall, IPv6 vs IPv4).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketHandshakeLoggingFilter implements WebFilter {

  private static final Logger log = LoggerFactory.getLogger(WebSocketHandshakeLoggingFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if ("/ws".equals(path)) {
      log.info(
          "WS HTTP request method={} upgrade={} connection={} origin={} remote={} hasQuery={}",
          exchange.getRequest().getMethod(),
          exchange.getRequest().getHeaders().getFirst("Upgrade"),
          exchange.getRequest().getHeaders().getFirst("Connection"),
          exchange.getRequest().getHeaders().getFirst("Origin"),
          exchange.getRequest().getRemoteAddress(),
          exchange.getRequest().getURI().getQuery() != null);
    }
    return chain.filter(exchange);
  }
}
