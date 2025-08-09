package com.gabcytn.shortnotice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class StompSendPayload {
  @NotNull(message = "Conversation id is required.")
  private UUID conversationId;

  @NotNull(message = "Message is required.")
  @NotBlank(message = "Message must not be blank.")
  private String message;

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "StompSendPayload{"
        + "conversationId="
        + conversationId
        + ", message='"
        + message
        + '\''
        + '}';
  }
}
