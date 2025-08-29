package com.gabcytn.strangerstrings.Model;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "userInterest")
public class UsersInterest {
  @Id private UUID userId;
  private Set<String> interests;

  public UsersInterest(UUID userId, Set<String> interests) {
    this.userId = userId;
    this.interests = interests;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Set<String> getInterests() {
    return interests;
  }

  public void setInterests(Set<String> interests) {
    this.interests = interests;
  }
}
