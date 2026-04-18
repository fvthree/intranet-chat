package com.intranet.chat.user;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/me")
  public Mono<UserResponse> currentUser() {
    return ReactiveSecurityContextHolder.getContext()
        .map(ctx -> (Jwt) ctx.getAuthentication().getPrincipal())
        .map(Jwt::getSubject)
        .map(UUID::fromString)
        .flatMap(userRepository::findById)
        .map(UserResponse::fromEntity)
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
  }
}
