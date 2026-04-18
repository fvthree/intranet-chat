package com.intranet.chat.presence;

import java.util.UUID;

public record PresenceStatusResponse(UUID userId, String status) {}
