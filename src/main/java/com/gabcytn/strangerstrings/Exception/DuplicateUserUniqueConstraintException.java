package com.gabcytn.strangerstrings.Exception;

public class DuplicateUserUniqueConstraintException extends RuntimeException {
  public DuplicateUserUniqueConstraintException() {}

  public DuplicateUserUniqueConstraintException(String message) {
    super(message);
  }
}
