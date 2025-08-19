package com.gabcytn.strangerstrings.Model;


public class ConversationMemberDetails {
  private String userId;
  private String username;
  private String profilePic;

  public ConversationMemberDetails(String userId, String username, String profilePic) {
    this.userId = userId;
    this.username = username;
    this.profilePic = profilePic;
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
}
