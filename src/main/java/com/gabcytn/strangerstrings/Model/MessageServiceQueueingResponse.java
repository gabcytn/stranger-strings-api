package com.gabcytn.strangerstrings.Model;

import java.util.Set;
import java.util.UUID;

public class MessageServiceQueueingResponse {
  private String interest;
  private Set<ConversationMemberDetails> members;
  private UUID conversationId;

  public MessageServiceQueueingResponse(
      String interest, Set<ConversationMemberDetails> members, UUID conversationId) {
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

  public Set<ConversationMemberDetails> getMembers() {
    return members;
  }

  public void setMembers(Set<ConversationMemberDetails> members) {
    this.members = members;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversation(UUID conversationId) {
    this.conversationId = conversationId;
  }

  @Override
  public String toString() {
    return "MessageServiceQueueingResponse{"
        + "interest='"
        + interest
        + '\''
        + ", members="
        + members
        + ", conversation="
        + conversationId
        + '}';
  }
}
