package com.pubfinder.pubfinder.exception;

import java.io.Serial;

public class ReviewAlreadyExistsException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  public ReviewAlreadyExistsException(String message) {
    super(message);
  }
}
