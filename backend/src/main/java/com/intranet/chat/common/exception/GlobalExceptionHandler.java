package com.intranet.chat.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Order(0)
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResponseStatusException.class)
  Mono<ResponseEntity<ProblemDetail>> handleResponseStatus(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, ex.getReason());
    return Mono.just(ResponseEntity.status(status).body(detail));
  }

  @ExceptionHandler(Exception.class)
  Mono<ResponseEntity<ProblemDetail>> handleGeneric(Exception ex) {
    log.error("Unhandled exception", ex);
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    return Mono.just(ResponseEntity.internalServerError().body(detail));
  }
}
