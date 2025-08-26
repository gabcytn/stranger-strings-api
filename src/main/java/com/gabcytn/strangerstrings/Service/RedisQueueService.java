package com.gabcytn.strangerstrings.Service;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisQueueService {
  private static final Logger LOG = LoggerFactory.getLogger(RedisQueueService.class);
  private static String USERS_TO_INTERESTS_MAP_REDIS_KEY;

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate;

  public RedisQueueService(
      @Qualifier("redisQueueTemplate") RedisTemplate<String, Object> redisTemplate,
      @Qualifier("redisUsersInterestsMapTemplate")
          RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate) {
    this.redisTemplate = redisTemplate;
    this.redisUsersInterestsMapTemplate = redisUsersInterestsMapTemplate;
  }

  public boolean interestQueueIsEmpty(String interest) {
    Set<Object> interestsSet = redisTemplate.opsForSet().members("interest:" + interest);
    assert interestsSet != null;
    return interestsSet.isEmpty();
  }

  public void placeUserInInterestsSet(List<String> interestsWithoutMatches, String sessionId) {
    for (String interest : interestsWithoutMatches) {
      redisTemplate.opsForSet().add("interest:" + interest, sessionId);
    }
    Map<String, List<String>> map = getUserInterestsListMap();
    assert map != null;
    map.put(sessionId, interestsWithoutMatches);
    setUserInterestsListMap(map);
  }

  public String getRandomMemberFromInterest(String interest) {
    return (String) redisTemplate.opsForSet().randomMember("interest:" + interest);
  }

  public void removeUserFromInterests(String sessionId) {
    try {
      Map<String, List<String>> interestsMap = getUserInterestsListMap();
      assert interestsMap != null;
      List<String> interestsList = interestsMap.get(sessionId);
      if (interestsList != null && !interestsList.isEmpty()) {
        // remove user from all their interests set
        interestsList.forEach(
            interest -> redisTemplate.opsForSet().remove("interest:" + interest, sessionId));
        // remove user from usersInterestsMap
        interestsMap.remove(sessionId);
        setUserInterestsListMap(interestsMap);
      }
    } catch (Exception e) {
      LOG.error("Error removing user from interests set");
      LOG.error(e.getMessage());
      LOG.error(Arrays.toString(e.getStackTrace()));
    }
  }

  public Boolean isMemberOfConversation(UUID userId, UUID conversationId) {
    Set<Object> conversationMembers =
        redisTemplate.opsForSet().members("members:" + conversationId.toString());
    assert conversationMembers != null;
    return conversationMembers.contains(userId.toString());
  }

  public Set<Object> getConversationMembers(UUID conversationId) {
    return redisTemplate.opsForSet().members("members:" + conversationId.toString());
  }

  private Map<String, List<String>> getUserInterestsListMap() {
    return redisUsersInterestsMapTemplate.opsForValue().get(USERS_TO_INTERESTS_MAP_REDIS_KEY);
  }

  private void setUserInterestsListMap(Map<String, List<String>> map) {
    redisUsersInterestsMapTemplate.opsForValue().set(USERS_TO_INTERESTS_MAP_REDIS_KEY, map);
  }

  @Value("${spring.data.redis.users-interests-map}")
  public void setUsersToInterestsMapRedisKey(String key) {
    USERS_TO_INTERESTS_MAP_REDIS_KEY = key;
  }
}
