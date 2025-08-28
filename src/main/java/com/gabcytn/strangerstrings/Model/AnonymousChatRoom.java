package com.gabcytn.strangerstrings.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "anonChatRoom")
public class AnonymousChatRoom {
  @TimeToLive private final Long expiresAt = 68L * 60 * 24 * 3; // 3 days TTL
  @Id private UUID conversationId;
  private Set<ConversationMember> participants;
  private List<AnonymousChatMessage> messages;

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

  public List<AnonymousChatMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<AnonymousChatMessage> messages) {
    this.messages = messages;
  }
}
