package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Model.RedisAnonymousMessage;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("REDIS")
public class RedisMessageStorageService implements MessageStorageService {
  private final RedisTemplate<String, Object> redisTemplate;

  public RedisMessageStorageService(
      @Qualifier("redisQueueTemplate") RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public void save(String message, User sender, Conversation conversation) {
    String key = "messages:" + conversation.getId().toString();
    redisTemplate.opsForList().rightPush(key, RedisAnonymousMessage.of(sender.getId(), message));
    // persist for 7 days for security purposes
    redisTemplate.expire(key, 7, TimeUnit.DAYS);
  }
}
