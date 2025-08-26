package com.gabcytn.strangerstrings.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "anonConversation:")
public class AnonymousConversation {
  @TimeToLive private final Long expiresAt = 68L * 60 * 24 * 3; // 3 days TTL
  @Id private UUID conversationId;
  private Set<ConversationMemberDetails> participants;
  private List<AnonymousMessage> messages;

  public AnonymousConversation(UUID conversationId, Set<ConversationMemberDetails> participants) {
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

  public Set<ConversationMemberDetails> getParticipants() {
    return participants;
  }

  public void setParticipants(Set<ConversationMemberDetails> participants) {
    this.participants = participants;
  }

  public List<AnonymousMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<AnonymousMessage> messages) {
    this.messages = messages;
  }
}
