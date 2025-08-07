package com.gabcytn.shortnotice.Service;

import com.gabcytn.shortnotice.Entity.Conversation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatQueueService {
  private static final Logger LOG = LoggerFactory.getLogger(ChatQueueService.class);
  private static String USERS_TO_INTERESTS_MAP_REDIS_KEY;

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public ChatQueueService(
      @Qualifier("redisQueueTemplate") RedisTemplate<String, Object> redisTemplate,
      @Qualifier("redisUsersInterestsMapTemplate")
          RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.redisTemplate = redisTemplate;
    this.redisUsersInterestsMapTemplate = redisUsersInterestsMapTemplate;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  void match(List<Object> sessionIds, Conversation conversation) {
    for (Object sessionId : sessionIds) {
      removeUserFromInterests((String) sessionId);
      simpMessagingTemplate.convertAndSendToUser(
          (String) sessionId,
          "/topic/anonymous/queue",
          "This is your conversation id: " + conversation.getId());
    }
    LOG.info("Successfully matched {} users.", sessionIds.size());
  }

  private void removeUserFromInterests(String sessionId) {
    try {
      Map<String, List<String>> interestsMap =
          redisUsersInterestsMapTemplate.opsForValue().get(USERS_TO_INTERESTS_MAP_REDIS_KEY);
      assert interestsMap != null;
      List<String> interestsList = interestsMap.get(sessionId);
      if (interestsList != null && !interestsList.isEmpty()) {
        // remove user from all their interests set
        interestsList.forEach(interest -> redisTemplate.opsForSet().remove(interest, sessionId));
        // remove user from usersInterestsMap
        Map<String, List<String>> map = redisUsersInterestsMapTemplate.opsForValue().get(USERS_TO_INTERESTS_MAP_REDIS_KEY);
				assert map != null;
				map.remove(sessionId);
        redisUsersInterestsMapTemplate.opsForValue().set(USERS_TO_INTERESTS_MAP_REDIS_KEY, map);
      }
    } catch (Exception e) {
      LOG.error("Error removing user from interests set");
      LOG.error(e.getMessage());
      LOG.error(Arrays.toString(e.getStackTrace()));
    }
  }

  @Value("${spring.data.redis.users-interests-map}")
  public void setUsersToInterestsMapRedisKey(String key) {
    USERS_TO_INTERESTS_MAP_REDIS_KEY = key;
  }
}
