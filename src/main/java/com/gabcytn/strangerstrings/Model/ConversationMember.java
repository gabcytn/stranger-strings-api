package com.gabcytn.strangerstrings.Model;

import java.util.UUID;

public class ConversationMember {
  private UUID id;

  public ConversationMember(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
