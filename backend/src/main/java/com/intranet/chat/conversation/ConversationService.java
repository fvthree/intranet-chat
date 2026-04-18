package com.intranet.chat.conversation;

import com.intranet.chat.user.User;
import com.intranet.chat.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class ConversationService {

  private static final String TYPE_DIRECT = "DIRECT";
  private static final String TYPE_CHANNEL = "CHANNEL";

  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final ConversationListingQuery conversationListingQuery;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  public ConversationService(
      UserRepository userRepository,
      ConversationRepository conversationRepository,
      ConversationParticipantRepository participantRepository,
      ConversationListingQuery conversationListingQuery,
      R2dbcEntityTemplate r2dbcEntityTemplate) {
    this.userRepository = userRepository;
    this.conversationRepository = conversationRepository;
    this.participantRepository = participantRepository;
    this.conversationListingQuery = conversationListingQuery;
    this.r2dbcEntityTemplate = r2dbcEntityTemplate;
  }

  public Mono<List<ConversationListItemResponse>> listForUser(UUID userId) {
    return conversationListingQuery.listForUser(userId).collectList();
  }

  public Mono<ConversationResponse> createChannel(UUID currentUserId, CreateChannelRequest request) {
    String trimmed = request.name().trim();
    if (trimmed.isEmpty()) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.BAD_REQUEST, "Channel name cannot be blank"));
    }
    Instant now = Instant.now();
    UUID convId = UUID.randomUUID();
    Conversation conv =
        new Conversation(convId, TYPE_CHANNEL, trimmed, null, currentUserId, now, now);
    ConversationParticipant creator =
        new ConversationParticipant(
            UUID.randomUUID(), convId, currentUserId, now, null, null);
    // Application-assigned IDs: CrudRepository.save() issues UPDATE; use insert for new rows.
    return r2dbcEntityTemplate
        .insert(Conversation.class)
        .using(conv)
        .flatMap(
            saved ->
                r2dbcEntityTemplate
                    .insert(ConversationParticipant.class)
                    .using(creator)
                    .thenReturn(saved))
        .map(ConversationResponse::from);
  }

  public Mono<ConversationResponse> createOrOpenDirect(UUID currentUserId, UUID otherUserId) {
    if (otherUserId.equals(currentUserId)) {
      return Mono.error(
          new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Cannot open a direct conversation with yourself"));
    }
    return userRepository
        .findById(otherUserId)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
        .filter(User::active)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is inactive")))
        .flatMap(
            ignored -> {
              String pairKey = DirectPairKey.of(currentUserId, otherUserId);
              return conversationRepository
                  .findByDirectPairKey(pairKey)
                  .map(ConversationResponse::from)
                  .switchIfEmpty(
                      createDirectConversation(currentUserId, otherUserId, pairKey)
                          .map(ConversationResponse::from));
            });
  }

  private Mono<Conversation> createDirectConversation(
      UUID currentUserId, UUID otherUserId, String pairKey) {
    Instant now = Instant.now();
    UUID convId = UUID.randomUUID();
    Conversation conv = new Conversation(convId, TYPE_DIRECT, null, pairKey, currentUserId, now, now);
    ConversationParticipant p1 =
        new ConversationParticipant(UUID.randomUUID(), convId, currentUserId, now, null, null);
    ConversationParticipant p2 =
        new ConversationParticipant(UUID.randomUUID(), convId, otherUserId, now, null, null);
    return r2dbcEntityTemplate
        .insert(Conversation.class)
        .using(conv)
        .flatMap(
            saved ->
                r2dbcEntityTemplate
                    .insert(ConversationParticipant.class)
                    .using(p1)
                    .then(r2dbcEntityTemplate.insert(ConversationParticipant.class).using(p2))
                    .thenReturn(saved));
  }

  public Mono<ConversationResponse> getForParticipant(UUID conversationId, UUID userId) {
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
        .map(ConversationResponse::from);
  }
}
