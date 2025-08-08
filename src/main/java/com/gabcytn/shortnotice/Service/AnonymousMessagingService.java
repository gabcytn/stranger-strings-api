package com.gabcytn.shortnotice.Service;

import com.gabcytn.shortnotice.DAO.ConversationDao;
import com.gabcytn.shortnotice.DTO.ChatInitiationDto;
import com.gabcytn.shortnotice.Entity.Conversation;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnonymousMessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingService.class);
  private final ConversationDao conversationDao;
  private final RedisQueueService redisQueueService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public AnonymousMessagingService(
      ConversationDao conversationDao,
      RedisQueueService redisQueueService,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.conversationDao = conversationDao;
    this.redisQueueService = redisQueueService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public void queue(ChatInitiationDto chatInitiationDto, String simpSessionId) {
    boolean hasMatch = false;
    if (chatInitiationDto.getInterests().isEmpty()) {
      // TODO: add to 'random' queue
    }
    List<String> interestsWithoutMatches = new ArrayList<>();
    for (String interest : chatInitiationDto.getInterests()) {
      // queue for current interest does not exist
      if (redisQueueService.interestQueueIsEmpty(interest)) {
        interestsWithoutMatches.add(interest);
        continue;
      }

      String matchedSessionId = redisQueueService.getRandomMemberFromInterest(interest);
      match(List.of(simpSessionId, matchedSessionId), conversationDao.save(new Conversation()));
      hasMatch = true;
      LOG.info("Match found: {}, {}; Interest: {}", simpSessionId, matchedSessionId, interest);
      break;
    }

    if (!hasMatch && !interestsWithoutMatches.isEmpty()) {
      redisQueueService.placeUserInInterestsSet(interestsWithoutMatches, simpSessionId);
      LOG.info("No match found for interests: {}", interestsWithoutMatches);
    }
  }

  private void match(List<String> sessionIds, Conversation conversation) {
    for (String sessionId : sessionIds) {
      redisQueueService.removeUserFromInterests(sessionId);
      simpMessagingTemplate.convertAndSendToUser(
              sessionId,
              "/topic/anonymous/queue",
              "This is your conversation id: " + conversation.getId());
    }
    LOG.info("Successfully matched {} users.", sessionIds.size());
  }
}
