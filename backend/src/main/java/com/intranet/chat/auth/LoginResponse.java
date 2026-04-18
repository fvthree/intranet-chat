package com.intranet.chat.auth;

public record LoginResponse(
    String accessToken, String tokenType, long expiresInSeconds) {}
