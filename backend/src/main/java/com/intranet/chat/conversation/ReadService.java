package com.intranet.chat.conversation;

import com.intranet.chat.message.Message;
import com.intranet.chat.message.MessageRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class ReadService {

  private final ConversationParticipantRepository participantRepository;
  private final MessageRepository messageRepository;

  public ReadService(
      ConversationParticipantRepository participantRepository,
      MessageRepository messageRepository) {
    this.participantRepository = participantRepository;
    this.messageRepository = messageRepository;
  }

  /**
   * Updates this user's read cursor for the conversation. Only affects the calling user's row in
   * {@code conversation_participants}.
   */
  public Mono<MarkReadResponse> markRead(UUID conversationId, UUID userId, MarkReadRequest request) {
    UUID requestedMessageId = request != null ? request.messageId() : null;
    return participantRepository
        .findByConversationIdAndUserId(conversationId, userId)
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not a participant of this conversation")))
        .flatMap(
            participant -> {
              if (requestedMessageId != null) {
                return messageRepository
                    .findByIdAndConversationId(requestedMessageId, conversationId)
                    .filter(m -> !m.deleted())
                    .switchIfEmpty(
                        Mono.error(
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")))
                    .flatMap(m -> persistReadPointer(participant, m));
              }
              return messageRepository
                  .findFirstByConversationIdAndDeletedIsFalseOrderByCreatedAtDesc(conversationId)
                  .flatMap(m -> persistReadPointer(participant, m))
                  .switchIfEmpty(
                      Mono.fromCallable(
                          () ->
                              new MarkReadResponse(
                                  participant.lastReadMessageId(), participant.lastReadAt())));
            });
  }

  private Mono<MarkReadResponse> persistReadPointer(ConversationParticipant participant, Message m) {
    Instant now = Instant.now();
    ConversationParticipant updated =
        new ConversationParticipant(
            participant.id(),
            participant.conversationId(),
            participant.userId(),
            participant.joinedAt(),
            m.id(),
            now);
    return participantRepository
        .save(updated)
        .map(saved -> new MarkReadResponse(saved.lastReadMessageId(), saved.lastReadAt()));
  }
}
