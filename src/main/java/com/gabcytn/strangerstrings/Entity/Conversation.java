package com.gabcytn.strangerstrings.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "conversations")
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Timestamp createdAt;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Message> messages;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Set<Message> getMessages() {
    return messages;
  }

  public void setMessages(Set<Message> messages) {
    this.messages = messages;
  }

  @Override
  public String toString() {
    return "Conversation{" + "id=" + id + ", createdAt=" + createdAt + '}';
  }
}
