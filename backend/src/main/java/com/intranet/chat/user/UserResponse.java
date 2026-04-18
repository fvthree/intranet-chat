package com.intranet.chat.user;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String employeeId,
    String username,
    String displayName,
    String email,
    String department,
    String role,
    boolean active) {

  public static UserResponse fromEntity(User u) {
    return new UserResponse(
        u.id(),
        u.employeeId(),
        u.username(),
        u.displayName(),
        u.email(),
        u.department(),
        u.role(),
        u.active());
  }
}
