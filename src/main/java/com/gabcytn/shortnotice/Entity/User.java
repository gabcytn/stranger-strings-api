package com.gabcytn.shortnotice.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String username;
  private String email;

  @Column(name = "profile_pic")
  private String profilePic = "default.png";

  private String password;

  public User(UUID id, String username, String email, String profilePic, String password) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.profilePic = profilePic;
    this.password = password;
  }

  public User() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getProfilePic() {
    return profilePic;
  }

  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", username='"
        + username
        + '\''
        + ", email='"
        + email
        + '\''
        + ", profilePic='"
        + profilePic
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }
}
