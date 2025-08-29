package com.gabcytn.strangerstrings.Model;

import java.util.Set;
import java.util.UUID;

public class MessageAndReceivers {
  private ChatMessage chatMessage;
  private Set<UUID> receivers;

  public MessageAndReceivers(ChatMessage chatMessage, Set<UUID> receivers) {
    this.chatMessage = chatMessage;
    this.receivers = receivers;
  }

  public ChatMessage getChatMessage() {
    return chatMessage;
  }

  public void setChatMessage(ChatMessage chatMessage) {
    this.chatMessage = chatMessage;
  }

  public Set<UUID> getReceivers() {
    return receivers;
  }

  public void setReceivers(Set<UUID> receivers) {
    this.receivers = receivers;
  }
}
