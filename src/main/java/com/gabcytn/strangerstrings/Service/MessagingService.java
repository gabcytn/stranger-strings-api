package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Model.MessageServiceQueueingResponse;
import java.security.Principal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingService.class);
  private final RedisQueueService redisQueueService;
  private final MessageStorageService dbMessageStorageService;
  private final MessageStorageService redisMessageStorageService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final ConversationService conversationService;
  private final UserService userService;

  public MessagingService(
      @Qualifier("DATABASE") MessageStorageService dbMessageStorageService,
      @Qualifier("REDIS") MessageStorageService redisMessageStorageService,
      RedisQueueService redisQueueService,
      ConversationService conversationService,
      UserService userService,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.dbMessageStorageService = dbMessageStorageService;
    this.redisMessageStorageService = redisMessageStorageService;
    this.redisQueueService = redisQueueService;
    this.conversationService = conversationService;
    this.userService = userService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  public Optional<MessageServiceQueueingResponse> queue(
      ChatInitiationDto chatInitiationDto, String simpSessionId) {
    String prefix = simpSessionId.substring(0, 5); // get auth || anon prefix
    List<String> interestsWithoutMatches = new ArrayList<>();
    for (String plainInterest : chatInitiationDto.getInterests()) {
      String interest = prefix + plainInterest;
      // queue for current interest does not exist
      if (redisQueueService.interestQueueIsEmpty(interest)) {
        interestsWithoutMatches.add(interest);
        continue;
      }

      String matchedSessionId = redisQueueService.getRandomMemberFromInterest(interest);
      if (matchedSessionId.equals(simpSessionId)) return Optional.empty(); // do not match with oneself
      Conversation conversation = conversationService.create();
      List<String> conversationMembers = List.of(simpSessionId, matchedSessionId);
      redisQueueService.placeInConversationMembers(conversation, conversationMembers);
      LOG.info("Match found: {}, {}; Interest: {}", simpSessionId, matchedSessionId, interest);
      return Optional.of(
          new MessageServiceQueueingResponse(
              interest,
              redisQueueService.getConversationMembers(conversation.getId()),
              conversation));
    }

    if (!interestsWithoutMatches.isEmpty()) {
      redisQueueService.placeUserInInterestsSet(interestsWithoutMatches, simpSessionId);
      LOG.info("No match found for interests: {}", interestsWithoutMatches);
    }
    return Optional.empty();
  }

  public void removeFromInterestsSet(String sessionId) {
    redisQueueService.removeUserFromInterests(sessionId);
  }

  public void message(String body, Principal sender, Conversation conversation) {
    this.persistMessage(body, sender, conversation);
    this.sendMessage(body, sender, conversation);
  }

  private void persistMessage(String body, Principal sender, Conversation conversation) {
    Optional<User> user = userService.findUserById(UUID.fromString(sender.getName()));
    if (user.isEmpty()) {
      User anonymousUser = new User();
      anonymousUser.setId(UUID.fromString(sender.getName()));
      redisMessageStorageService.save(body, anonymousUser, conversation);
    } else {
      dbMessageStorageService.save(body, user.get(), conversation);
    }
  }

  private void sendMessage(String body, Principal sender, Conversation conversation) {
    Set<Object> members = redisQueueService.getConversationMembers(conversation.getId());
    members.forEach(
        member -> {
          if (!sender.getName().equals(member.toString())) {
            simpMessagingTemplate.convertAndSendToUser(member.toString(), "/queue/chat", body);
          }
        });
  }
}
