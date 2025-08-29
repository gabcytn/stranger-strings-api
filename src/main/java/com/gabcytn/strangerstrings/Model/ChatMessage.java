package com.gabcytn.strangerstrings.Model;

public abstract class ChatMessage<T extends ConversationMember> {
  private T sender;
  private String message;

  protected ChatMessage(T sender, String message) {
    this.sender = sender;
    this.message = message;
  }

  public T getSender() {
    return sender;
  }

  public void setSender(T sender) {
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
