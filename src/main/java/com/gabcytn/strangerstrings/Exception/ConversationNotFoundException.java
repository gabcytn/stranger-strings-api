package com.gabcytn.strangerstrings.Exception;

public class ConversationNotFoundException extends RuntimeException {
  public ConversationNotFoundException() {}

  public ConversationNotFoundException(String message) {
    super(message);
  }
}
