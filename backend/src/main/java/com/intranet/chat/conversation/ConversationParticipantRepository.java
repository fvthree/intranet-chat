package com.intranet.chat.conversation;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ConversationParticipantRepository
    extends ReactiveCrudRepository<ConversationParticipant, UUID> {

  Mono<Boolean> existsByConversationIdAndUserId(UUID conversationId, UUID userId);
}
