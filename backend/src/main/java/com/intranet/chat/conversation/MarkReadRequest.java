package com.intranet.chat.conversation;

import java.util.UUID;

/** Optional {@code messageId}: when omitted or null, marks up to the latest message in the conversation. */
public record MarkReadRequest(UUID messageId) {}
