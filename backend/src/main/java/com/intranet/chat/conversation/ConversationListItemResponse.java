package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;

public record ConversationListItemResponse(
    UUID id,
    String type,
    String name,
    UUID createdBy,
    Instant createdAt,
    Instant updatedAt,
    long unreadCount,
    LastMessagePreview lastMessage) {}
