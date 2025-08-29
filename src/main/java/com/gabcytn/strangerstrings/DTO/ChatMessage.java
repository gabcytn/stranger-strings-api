package com.gabcytn.strangerstrings.DTO;

import java.util.UUID;

public class ChatMessage {
  private UUID senderId;
  private String message;

  public ChatMessage(UUID senderId, String message) {
    this.senderId = senderId;
    this.message = message;
  }

  public UUID getSenderId() {
    return senderId;
  }

  public void setSenderId(UUID senderId) {
    this.senderId = senderId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
