package com.gabcytn.strangerstrings.Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "profile_pic", nullable = false)
  private String profilePic = "default.png";

  @Column(nullable = false)
  private String password;

  @Column(name = "email_verified_at")
  private Date emailVerifiedAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Date createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Date updatedAt;

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Message> messages;

  @ManyToMany(mappedBy = "members")
  private Set<Conversation> conversations;

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

  public Date getEmailVerifiedAt() {
    return emailVerifiedAt;
  }

  public void setEmailVerifiedAt(Date emailVerifiedAt) {
    this.emailVerifiedAt = emailVerifiedAt;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<Message> getMessages() {
    return messages;
  }

  public void setMessages(Set<Message> messages) {
    this.messages = messages;
  }

  public Set<Conversation> getConversations() {
    return conversations;
  }

  public void setConversations(Set<Conversation> conversations) {
    this.conversations = conversations;
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
        + ", emailVerifiedAt="
        + emailVerifiedAt
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
