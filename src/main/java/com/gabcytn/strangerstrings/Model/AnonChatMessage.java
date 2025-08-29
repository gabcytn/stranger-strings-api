package com.gabcytn.strangerstrings.Model;

public class AnonChatMessage extends ChatMessage<ConversationMember> {
  public AnonChatMessage(ConversationMember sender, String message) {
    super(sender, message);
  }
}
