package com.gabcytn.strangerstrings.DTO;

import com.gabcytn.strangerstrings.Model.ConversationMemberDetails;
import java.util.Set;
import java.util.UUID;

public class InterestMatchedResponse {
  private String interest;
  private UUID conversationId;
  private Set<ConversationMemberDetails> participants;

  public InterestMatchedResponse(
      String interest, UUID conversationId, Set<ConversationMemberDetails> participants) {
    this.interest = interest;
    this.conversationId = conversationId;
    this.participants = participants;
  }

  public String getInterest() {
    return interest;
  }

  public void setInterest(String interest) {
    this.interest = interest;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }

  public Set<ConversationMemberDetails> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<ConversationMemberDetails> participants) {
    this.participants = participants;
  }

  @Override
  public String toString() {
    return "InterestMatchedResponse{"
        + "interest='"
        + interest
        + '\''
        + ", conversationId="
        + conversationId
        + ", participants="
        + participants
        + '}';
  }
}
