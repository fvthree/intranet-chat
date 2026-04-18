package com.intranet.chat.conversation;

import com.intranet.chat.message.MessagePageResponse;
import com.intranet.chat.message.MessageResponse;
import com.intranet.chat.message.MessageService;
import com.intranet.chat.message.SendMessageRequest;
import com.intranet.chat.security.CurrentUserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/conversations")
@Validated
public class ConversationController {

  private final CurrentUserId currentUserId;
  private final ConversationService conversationService;
  private final MessageService messageService;

  public ConversationController(
      CurrentUserId currentUserId,
      ConversationService conversationService,
      MessageService messageService) {
    this.currentUserId = currentUserId;
    this.conversationService = conversationService;
    this.messageService = messageService;
  }

  @GetMapping
  public Mono<List<ConversationListItemResponse>> list() {
    return currentUserId.get().flatMap(conversationService::listForUser);
  }

  @PostMapping("/channels")
  public Mono<ConversationResponse> createChannel(@Valid @RequestBody CreateChannelRequest body) {
    return currentUserId.get().flatMap(uid -> conversationService.createChannel(uid, body));
  }

  @PostMapping("/direct")
  public Mono<ConversationResponse> createDirect(
      @Valid @RequestBody CreateDirectConversationRequest body) {
    return currentUserId
        .get()
        .flatMap(uid -> conversationService.createOrOpenDirect(uid, body.otherUserId()));
  }

  @GetMapping("/{conversationId}")
  public Mono<ConversationResponse> get(@PathVariable UUID conversationId) {
    return currentUserId.get().flatMap(uid -> conversationService.getForParticipant(conversationId, uid));
  }

  @PostMapping("/{conversationId}/messages")
  public Mono<MessageResponse> sendMessage(
      @PathVariable UUID conversationId, @Valid @RequestBody SendMessageRequest body) {
    return currentUserId.get().flatMap(uid -> messageService.send(conversationId, uid, body));
  }

  @GetMapping("/{conversationId}/messages")
  public Mono<MessagePageResponse> listMessages(
      @PathVariable UUID conversationId,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
    return currentUserId
        .get()
        .flatMap(uid -> messageService.listMessages(conversationId, uid, page, size));
  }
}
