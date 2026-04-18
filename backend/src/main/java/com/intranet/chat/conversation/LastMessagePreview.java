package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;

public record LastMessagePreview(
    UUID messageId, String contentPreview, UUID senderId, Instant createdAt) {}
