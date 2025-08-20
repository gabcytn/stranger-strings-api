package com.gabcytn.strangerstrings.Model;

import java.util.Date;
import java.util.UUID;

public class PaginatedMessageContent {
  private Integer id;
  private String body;
  private ConversationMemberDetails sender;
  private UUID conversationId;
  private Date date;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ConversationMemberDetails getSender() {
    return sender;
  }

  public void setSender(ConversationMemberDetails sender) {
    this.sender = sender;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
