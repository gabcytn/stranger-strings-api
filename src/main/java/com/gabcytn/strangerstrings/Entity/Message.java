package com.gabcytn.strangerstrings.Entity;

import jakarta.persistence.*;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "messages",
    indexes = {@Index(columnList = "conversation_id")})
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  private String body;

  @ManyToOne
  @JoinColumn(name = "sender_id", referencedColumnName = "id")
  private User sender;

  @ManyToOne(optional = false)
  @JoinColumn(name = "conversation_id", referencedColumnName = "id")
  private Conversation conversation;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Date createdAt;

  public Message() {}

  public Message(String body, User sender, Conversation conversation, Date createdAt) {
    this.body = body;
    this.sender = sender;
    this.conversation = conversation;
    this.createdAt = createdAt;
  }

  public Message(Integer id, String body, User sender, Conversation conversation, Date createdAt) {
    this.id = id;
    this.body = body;
    this.sender = sender;
    this.conversation = conversation;
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public void setConversation(Conversation conversation) {
    this.conversation = conversation;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
}
