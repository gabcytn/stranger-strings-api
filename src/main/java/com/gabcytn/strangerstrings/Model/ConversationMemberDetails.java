package com.gabcytn.strangerstrings.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ConversationMemberDetails {
  private String userId;
  private String username;
  private String profilePic;

  @JsonCreator
  public ConversationMemberDetails(
      @JsonProperty("userId") String userId,
      @JsonProperty("username") String username,
      @JsonProperty("profilePic") String profilePic) {
    this.userId = userId;
    this.username = username;
    this.profilePic = profilePic;
  }

  public ConversationMemberDetails(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  @Override
  public String toString() {
    return "ConversationMemberDetails{"
        + "username='"
        + username
        + '\''
        + ", profilePic='"
        + profilePic
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ConversationMemberDetails that = (ConversationMemberDetails) o;
    return Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(userId);
  }
}
