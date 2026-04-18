package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("conversation_participants")
public record ConversationParticipant(
    @Id UUID id,
    UUID conversationId,
    UUID userId,
    Instant joinedAt,
    UUID lastReadMessageId,
    Instant lastReadAt) {}
