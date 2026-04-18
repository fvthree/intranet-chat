package com.intranet.chat.presence;

import com.intranet.chat.realtime.PresenceService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/presence")
public class PresenceController {

  private final PresenceService presenceService;

  public PresenceController(PresenceService presenceService) {
    this.presenceService = presenceService;
  }

  @GetMapping("/{userId}")
  public Mono<PresenceStatusResponse> get(@PathVariable UUID userId) {
    return presenceService
        .isOnline(userId)
        .map(
            online ->
                new PresenceStatusResponse(
                    userId, Boolean.TRUE.equals(online) ? "ONLINE" : "OFFLINE"));
  }
}
