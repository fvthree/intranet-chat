package com.intranet.chat.message;

import com.intranet.chat.conversation.Conversation;
import com.intranet.chat.conversation.ConversationParticipantRepository;
import com.intranet.chat.conversation.ConversationRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class MessageService {

  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final MessageRepository messageRepository;

  public MessageService(
      ConversationRepository conversationRepository,
      ConversationParticipantRepository participantRepository,
      MessageRepository messageRepository) {
    this.conversationRepository = conversationRepository;
    this.participantRepository = participantRepository;
    this.messageRepository = messageRepository;
  }

  public Mono<MessageResponse> send(UUID conversationId, UUID senderId, SendMessageRequest request) {
    String trimmed = request.content().trim();
    if (trimmed.isEmpty()) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content cannot be blank"));
    }
    String content = trimmed;
    return conversationRepository
        .findById(conversationId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found")))
        .flatMap(
            conv ->
                participantRepository
                    .existsByConversationIdAndUserId(conversationId, senderId)
                    .flatMap(
                        ok ->
                            Boolean.TRUE.equals(ok)
                                ? Mono.just(conv)
                                : Mono.error(
                                    new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Not a participant of this conversation"))))
        .then(
            Mono.defer(
                () -> {
                  Instant now = Instant.now();
                  UUID id = UUID.randomUUID();
                  Message m =
                      new Message(id, conversationId, senderId, content, now, now, false);
                  return messageRepository
                      .save(m)
                      .flatMap(saved -> touchConversation(conversationId, now).thenReturn(saved));
                }))
        .map(MessageResponse::from);
  }

  private Mono<Void> touchConversation(UUID conversationId, Instant now) {
    return conversationRepository
        .findById(conversationId)
        .flatMap(
            c ->
                conversationRepository.save(
                    new Conversation(
                        c.id(),
                        c.type(),
                        c.name(),
                        c.directPairKey(),
                        c.createdBy(),
                        c.createdAt(),
                        now)))
        .then();
  }

  public Mono<MessagePageResponse> listMessages(
      UUID conversationId, UUID userId, int page, int size) {
    return conversationRepository
        .findById(conversationId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found")))
        .flatMap(
            conv ->
                participantRepository
                    .existsByConversationIdAndUserId(conversationId, userId)
                    .flatMap(
                        ok ->
                            Boolean.TRUE.equals(ok)
                                ? Mono.just(conv)
                                : Mono.error(
                                    new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Not a participant of this conversation"))))
        .then(
            Mono.zip(
                messageRepository.countByConversationIdAndDeletedIsFalse(conversationId),
                messageRepository
                    .findByConversationIdAndDeletedIsFalseOrderByCreatedAtAsc(
                        conversationId, PageRequest.of(page, size))
                    .map(MessageResponse::from)
                    .collectList()))
        .map(
            tuple -> {
              long total = tuple.getT1();
              var list = tuple.getT2();
              boolean hasNext = (page + 1L) * (long) size < total;
              return new MessagePageResponse(list, page, size, total, hasNext);
            });
  }
}
