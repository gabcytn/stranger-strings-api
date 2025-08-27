package com.gabcytn.strangerstrings.Model;

import java.util.UUID;

public class AuthenticatedConversationMember extends ConversationMember {
  private String username;
  private String profilePic;

  public AuthenticatedConversationMember(UUID id) {
    super(id);
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
}
