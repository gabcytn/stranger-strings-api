package com.gabcytn.strangerstrings.Model;

import java.util.Set;
import java.util.UUID;

public class QueueMatchedResponse<T extends ConversationMember> {
  private String interest;
  private Set<T> members;
  private UUID conversationId;

  public QueueMatchedResponse(String interest, Set<T> members, UUID conversationId) {
    this.interest = interest;
    this.members = members;
    this.conversationId = conversationId;
  }

  public String getInterest() {
    return interest;
  }

  public void setInterest(String interest) {
    this.interest = interest;
  }

  public Set<T> getMembers() {
    return members;
  }

  public void setMembers(Set<T> members) {
    this.members = members;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }
}
