package com.gabcytn.strangerstrings.Model;

import com.gabcytn.strangerstrings.DTO.ChatMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "anonChatRoom")
public class AnonymousChatRoom {
  @TimeToLive private Long expiresAt = 68L * 60 * 24 * 3; // 3 days TTL
  @Id private UUID conversationId;
  private Set<ConversationMember> participants;
  private List<ChatMessage> messages;

  public AnonymousChatRoom(UUID conversationId, Set<ConversationMember> participants) {
    this.conversationId = conversationId;
    this.participants = participants;
    this.messages = new ArrayList<>();
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }

  public Set<ConversationMember> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<ConversationMember> participants) {
    this.participants = participants;
  }

  public List<ChatMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<ChatMessage> messages) {
    this.messages = messages;
  }

  public Long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Long expiresAt) {
    this.expiresAt = expiresAt;
  }
}
