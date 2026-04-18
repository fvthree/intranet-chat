package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
    UUID id,
    String type,
    String name,
    UUID createdBy,
    Instant createdAt,
    Instant updatedAt) {

  public static ConversationResponse from(Conversation c) {
    return new ConversationResponse(
        c.id(), c.type(), c.name(), c.createdBy(), c.createdAt(), c.updatedAt());
  }
}
