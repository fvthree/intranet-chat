package com.intranet.chat.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intranet.chat.conversation.ConversationParticipant;
import com.intranet.chat.conversation.ConversationParticipantRepository;
import com.intranet.chat.message.Message;
import com.intranet.chat.message.MessageResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RealtimeMessagePublisher {

  private final ConversationParticipantRepository participantRepository;
  private final RealtimeConnectionRegistry registry;
  private final ObjectMapper objectMapper;

  public RealtimeMessagePublisher(
      ConversationParticipantRepository participantRepository,
      RealtimeConnectionRegistry registry,
      ObjectMapper objectMapper) {
    this.participantRepository = participantRepository;
    this.registry = registry;
    this.objectMapper = objectMapper;
  }

  public Mono<Void> publishNewMessage(Message saved) {
    MessageResponse dto = MessageResponse.from(saved);
    Map<String, Object> envelope = new LinkedHashMap<>();
    envelope.put("type", "MESSAGE_NEW");
    envelope.put("conversationId", saved.conversationId().toString());
    envelope.put("message", dto);
    final String json;
    try {
      json = objectMapper.writeValueAsString(envelope);
    } catch (JsonProcessingException e) {
      return Mono.empty();
    }
    return participantRepository
        .findByConversationId(saved.conversationId())
        .map(ConversationParticipant::userId)
        .filter(uid -> !uid.equals(saved.senderId()))
        .flatMap(uid -> registry.sendToUser(uid, json))
        .then();
  }
}
