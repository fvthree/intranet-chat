package com.intranet.chat.conversation;

import java.time.Instant;
import java.util.UUID;

public record MarkReadResponse(UUID lastReadMessageId, Instant lastReadAt) {}
