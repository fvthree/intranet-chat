package com.intranet.chat.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class HealthController {

  @GetMapping("/health")
  public Mono<Map<String, String>> health() {
    return Mono.just(
        Map.of(
            "status", "up",
            "service", "intranet-chat"));
  }
}
