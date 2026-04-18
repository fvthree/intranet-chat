package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("conversations")
public record Conversation(
    @Id UUID id,
    String type,
    String name,
    String directPairKey,
    UUID createdBy,
    Instant createdAt,
    Instant updatedAt) {}
