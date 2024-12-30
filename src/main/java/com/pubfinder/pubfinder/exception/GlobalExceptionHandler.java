package com.pubfinder.pubfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
    // Log exception Message
    return new ResponseEntity<>("Resource Not Found", HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = BadRequestException.class)
  public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
    // Log exception Message
    if (ex.getMessage().isEmpty())
      return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    else
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = BadCredentialsException.class)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
    return new ResponseEntity<>("Bad Credentials", HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    // Log exception Message
    return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = ReviewAlreadyExistsException.class)
  public ResponseEntity<String> handleAccessDeniedException(ReviewAlreadyExistsException ex) {
    // Log exception Message
    return new ResponseEntity<>("User has already made a review on this Pub", HttpStatus.CONFLICT);
  }
}
