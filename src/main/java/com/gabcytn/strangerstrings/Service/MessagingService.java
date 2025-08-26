package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Cache.AnonymousConversationDao;
import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import com.gabcytn.strangerstrings.Model.*;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingService.class);
  private final RedisQueueService redisQueueService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final ConversationService conversationService;
  private final UserService userService;
  private final MessageDao messageDao;
  private final AnonymousConversationDao anonymousConversationDao;

  public MessagingService(
      RedisQueueService redisQueueService,
      ConversationService conversationService,
      UserService userService,
      SimpMessagingTemplate simpMessagingTemplate,
      MessageDao messageDao,
      AnonymousConversationDao anonymousConversationDao) {
    this.redisQueueService = redisQueueService;
    this.conversationService = conversationService;
    this.userService = userService;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.messageDao = messageDao;
    this.anonymousConversationDao = anonymousConversationDao;
  }

  private Optional<MessageServiceQueueingResponse> handleQueue(
      List<String> interests,
      String userId,
      Function<QueueHandlerResponse, Optional<MessageServiceQueueingResponse>> onMatchSuccess) {

    List<String> interestsWithoutMatches = new ArrayList<>();
    for (String interest : interests) {
      if (redisQueueService.interestQueueIsEmpty(interest)) {
        interestsWithoutMatches.add(interest);
        continue;
      }

      String matchedSessionId = redisQueueService.getRandomMemberFromInterest(interest);
      if (matchedSessionId.equals(userId)) return Optional.empty();

      redisQueueService.removeUserFromInterests(userId);
      redisQueueService.removeUserFromInterests(matchedSessionId);

      return onMatchSuccess.apply(new QueueHandlerResponse(interest, matchedSessionId));
    }

    if (!interestsWithoutMatches.isEmpty()) {
      redisQueueService.placeUserInInterestsSet(interestsWithoutMatches, userId.toString());
      LOG.info("No match found for interests: {}", interestsWithoutMatches);
    }
    return Optional.empty();
  }

  public Optional<MessageServiceQueueingResponse> queueOfAnonymous(
      List<String> interests, String userId) {
    return handleQueue(
        interests,
        userId,
        response -> {
          ConversationMemberDetails member1 =
              new ConversationMemberDetails(response.getMatchedUserId());
          ConversationMemberDetails member2 = new ConversationMemberDetails(userId);
          UUID conversationId = UUID.randomUUID();
          AnonymousConversation conversation =
              new AnonymousConversation(conversationId, Set.of(member1, member2));
          anonymousConversationDao.save(conversation);
          return Optional.of(
              new MessageServiceQueueingResponse(
                  response.getInterest(), Set.of(member1, member2), conversationId));
        });
  }

  public Optional<MessageServiceQueueingResponse> queueOfAuthenticated(
      List<String> interests, String userId) {
    return handleQueue(
        interests,
        userId,
        response -> {
          Conversation conversation = conversationService.create();
          ConversationMemberDetails member1 =
              new ConversationMemberDetails(response.getMatchedUserId());
          ConversationMemberDetails member2 = new ConversationMemberDetails(userId);
          conversation.setMembers(
              userService.getUserSetFromIdList(
                  List.of(UUID.fromString(userId), UUID.fromString(response.getMatchedUserId()))));
          conversationService.save(conversation);
          return Optional.of(
              new MessageServiceQueueingResponse(
                  response.getInterest(), Set.of(member1, member2), conversation.getId()));
        });
  }

  public void message(String body, Principal sender, Conversation conversation) {
    this.persistMessage(body, sender, conversation);
    this.sendMessage(body, sender, conversation);
  }

  private void persistMessage(String body, Principal sender, Conversation conversation) {
    Optional<User> user =
        userService.findUserById(UUID.fromString(sender.getName())); // FIX: substring prefix
    if (user.isEmpty()) {
      AnonymousConversation anonymousConversation =
          anonymousConversationDao.findById(conversation.getId()).orElseThrow();
      anonymousConversation
          .getMessages()
          .add(new AnonymousMessage(new ConversationMemberDetails(sender.getName()), body));
    } else {
      Message message = new Message(body, user.get(), conversation, new Date());
      messageDao.save(message);
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
