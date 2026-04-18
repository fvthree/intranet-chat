package com.intranet.chat.message;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveCrudRepository<Message, UUID> {

  Flux<Message> findByConversationIdAndDeletedIsFalseOrderByCreatedAtAsc(
      UUID conversationId, Pageable pageable);

  Mono<Long> countByConversationIdAndDeletedIsFalse(UUID conversationId);
}
