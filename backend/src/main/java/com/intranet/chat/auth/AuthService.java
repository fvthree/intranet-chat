package com.intranet.chat.auth;

import com.intranet.chat.security.JwtProperties;
import com.intranet.chat.user.User;
import com.intranet.chat.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final JwtProperties jwtProperties;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TokenService tokenService,
      JwtProperties jwtProperties) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.jwtProperties = jwtProperties;
  }

  public Mono<LoginResponse> login(LoginRequest request) {
    return userRepository
        .findByUsername(request.username())
        .filter(User::active)
        .filter(u -> passwordEncoder.matches(request.password(), u.passwordHash()))
        .map(
            user ->
                new LoginResponse(
                    tokenService.createAccessToken(user),
                    "Bearer",
                    jwtProperties.accessTokenExpirationSeconds()))
        .switchIfEmpty(
            Mono.error(
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password")));
  }
}
