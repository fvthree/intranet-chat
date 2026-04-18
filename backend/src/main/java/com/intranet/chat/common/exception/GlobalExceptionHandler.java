package com.intranet.chat.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Order(0)
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(WebExchangeBindException.class)
  Mono<ResponseEntity<ProblemDetail>> handleValidation(WebExchangeBindException ex) {
    String detail =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    return Mono.just(ResponseEntity.badRequest().body(pd));
  }

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
