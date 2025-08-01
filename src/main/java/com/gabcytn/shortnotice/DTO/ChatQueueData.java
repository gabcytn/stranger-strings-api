package com.gabcytn.shortnotice.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("chatQueueData")
public class ChatQueueData {
  @Indexed private final String value;
  @Id private String id;
  // defaults to no ttl;
  @TimeToLive private Long expiresAt = 0L;

  public ChatQueueData(String id, String value) {
    this.id = id;
    this.value = value;
  }

  public ChatQueueData(String id, String value, Long expiresAt) {
    this.id = id;
    this.value = value;
    this.expiresAt = expiresAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public Long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Long expiresAt) {
    this.expiresAt = expiresAt;
  }
}
