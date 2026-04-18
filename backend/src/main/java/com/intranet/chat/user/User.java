package com.intranet.chat.user;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
    @Id UUID id,
    String employeeId,
    String username,
    String displayName,
    String email,
    String department,
    String role,
    String passwordHash,
    boolean active,
    Instant createdAt,
    Instant updatedAt) {}
