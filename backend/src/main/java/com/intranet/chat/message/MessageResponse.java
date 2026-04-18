package com.intranet.chat.message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
    UUID id, UUID conversationId, UUID senderId, String content, Instant createdAt) {

  public static MessageResponse from(Message m) {
    return new MessageResponse(
        m.id(), m.conversationId(), m.senderId(), m.content(), m.createdAt());
  }
}
