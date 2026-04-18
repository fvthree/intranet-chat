package com.intranet.chat.message;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("messages")
public record Message(
    @Id UUID id,
    UUID conversationId,
    UUID senderId,
    String content,
    Instant createdAt,
    Instant updatedAt,
    boolean deleted) {}
