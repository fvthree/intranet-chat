package com.intranet.chat.conversation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDirectConversationRequest(@NotNull(message = "otherUserId is required") UUID otherUserId) {}
