package com.gabcytn.strangerstrings.Model;

public class AuthChatMessage extends ChatMessage<AuthenticatedConversationMember> {
  public AuthChatMessage(AuthenticatedConversationMember sender, String message) {
    super(sender, message);
  }
}
