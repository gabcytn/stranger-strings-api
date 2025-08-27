package com.gabcytn.strangerstrings.Model;

public class AnonymousChatMessage {
  private ConversationMember sender;
  private String message;

  public AnonymousChatMessage(ConversationMember sender, String message) {
    this.sender = sender;
    this.message = message;
  }

  public ConversationMember getSender() {
    return sender;
  }

  public void setSender(ConversationMember sender) {
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
