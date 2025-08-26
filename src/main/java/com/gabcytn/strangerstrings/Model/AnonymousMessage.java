package com.gabcytn.strangerstrings.Model;

public class AnonymousMessage {
  private ConversationMemberDetails sender;
  private String message;

  public AnonymousMessage(ConversationMemberDetails sender, String message) {
    this.sender = sender;
    this.message = message;
  }

  public ConversationMemberDetails getSender() {
    return sender;
  }

  public void setSender(ConversationMemberDetails sender) {
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
