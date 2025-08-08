package com.gabcytn.shortnotice.Service;

import com.gabcytn.shortnotice.DAO.ConversationDao;
import com.gabcytn.shortnotice.DTO.ChatInitiationDto;
import com.gabcytn.shortnotice.Entity.Conversation;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
public class AnonymousMessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingService.class);
  private static String USERS_TO_INTERESTS_MAP_REDIS_KEY;
  private final SetOperations<String, Object> setOperations;
  private final RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate;
  private final ConversationDao conversationDao;
  private final ChatQueueService chatQueueService;

  public AnonymousMessagingService(
      @Qualifier("redisQueueTemplate") RedisTemplate<String, Object> redisQueueTemplate,
      @Qualifier("redisUsersInterestsMapTemplate") RedisTemplate<String, Map<String, List<String>>> mapRedisTemplate,
      ConversationDao conversationDao,
      ChatQueueService chatQueueService) {
    this.conversationDao = conversationDao;
    this.chatQueueService = chatQueueService;
    this.redisUsersInterestsMapTemplate = mapRedisTemplate;
    this.setOperations = redisQueueTemplate.opsForSet();
  }

  public void queue(ChatInitiationDto chatInitiationDto, String simpSessionId) {
    boolean hasMatch = false;
    if (chatInitiationDto.getInterests().isEmpty()) {
      // TODO: add to 'random' queue
    }
    List<String> interestsWithoutMatches = new ArrayList<>();
    for (String interest : chatInitiationDto.getInterests()) {
      // queue for current interest does not exist
      if (interestQueueIsEmpty(interest)) {
        interestsWithoutMatches.add(interest);
        continue;
      }

      String matchedSessionId = (String) setOperations.randomMember(interest);
      chatQueueService.match(
          List.of(simpSessionId, matchedSessionId), conversationDao.save(new Conversation()));
      hasMatch = true;
      LOG.info("Match found: {}, {}; Interest: {}", simpSessionId, matchedSessionId, interest);
      break;
    }

    if (!hasMatch && !interestsWithoutMatches.isEmpty()) {
      placeUserInInterestsSet(interestsWithoutMatches, simpSessionId);
      LOG.info("No match found for interests: {}", interestsWithoutMatches);
    }
  }

  private boolean interestQueueIsEmpty(String interest) {
    Set<Object> interestsSet = setOperations.members(interest);
    assert interestsSet != null;
    return interestsSet.isEmpty();
  }

  private void placeUserInInterestsSet(List<String> interestsWithoutMatches, String sessionId) {
    for (String interest : interestsWithoutMatches) {
      setOperations.add(interest, sessionId);
    }
    Map<String, List<String>> map = redisUsersInterestsMapTemplate.opsForValue().get(USERS_TO_INTERESTS_MAP_REDIS_KEY);
		assert map != null;
		map.put(sessionId, interestsWithoutMatches);
    redisUsersInterestsMapTemplate.opsForValue().set(USERS_TO_INTERESTS_MAP_REDIS_KEY, map);
  }

  @Value("${spring.data.redis.users-interests-map}")
  public void setUsersToInterestsMapRedisKey(String key) {
    USERS_TO_INTERESTS_MAP_REDIS_KEY = key;
  }

}
