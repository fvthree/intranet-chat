package com.intranet.chat.message;

import java.util.List;

public record MessagePageResponse(
    List<MessageResponse> messages, int page, int size, long totalElements, boolean hasNext) {}
