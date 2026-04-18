package com.intranet.chat.conversation;

import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ConversationRepository extends ReactiveCrudRepository<Conversation, UUID> {

  Mono<Conversation> findByDirectPairKey(String directPairKey);
}
