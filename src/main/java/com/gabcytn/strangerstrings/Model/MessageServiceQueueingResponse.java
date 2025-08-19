package com.gabcytn.strangerstrings.Model;

import com.gabcytn.strangerstrings.Entity.Conversation;
import java.util.Set;

public class MessageServiceQueueingResponse {
  private String interest;
  private Set<Object> members;
  private Conversation conversation;

  public MessageServiceQueueingResponse(
          String interest, Set<Object> members, Conversation conversation) {
    this.interest = interest;
    this.members = members;
    this.conversation = conversation;
  }

  public MessageServiceQueueingResponse() {}

  public String getInterest() {
    return interest;
  }

  public void setInterest(String interest) {
    this.interest = interest;
  }

  public Set<Object> getMembers() {
    return members;
  }

  public void setMembers(Set<Object> members) {
    this.members = members;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public void setConversation(Conversation conversation) {
    this.conversation = conversation;
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
        + conversation
        + '}';
  }
}
