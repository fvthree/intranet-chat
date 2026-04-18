package com.intranet.chat.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChannelRequest(
    @NotBlank(message = "Channel name is required")
        @Size(max = 255, message = "Channel name must be at most 255 characters")
        String name) {}
