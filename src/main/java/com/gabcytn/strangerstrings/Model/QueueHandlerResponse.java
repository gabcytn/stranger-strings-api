package com.gabcytn.strangerstrings.Model;

public class QueueHandlerResponse {
  private String interest;
  private String matchedUserId;

  public QueueHandlerResponse(String interest, String matchedUserId) {
    this.interest = interest;
    this.matchedUserId = matchedUserId;
  }

  public String getInterest() {
    return interest;
  }

  public void setInterest(String interest) {
    this.interest = interest;
  }

  public String getMatchedUserId() {
    return matchedUserId;
  }

  public void setMatchedUserId(String matchedUserId) {
    this.matchedUserId = matchedUserId;
  }
}
