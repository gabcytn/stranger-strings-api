package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import com.gabcytn.strangerstrings.Model.*;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("AuthMessagingService")
public class AuthMessagingService implements MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AuthMessagingService.class);
  private final QueueService queueService;
  private final UserService userService;
  private final ConversationService conversationService;
  private final MessageDao messageDao;

  public AuthMessagingService(
      @Qualifier("AuthQueueService") QueueService queueService,
      UserService userService,
      ConversationService conversationService,
      MessageDao messageDao) {
    this.queueService = queueService;
    this.userService = userService;
    this.conversationService = conversationService;
    this.messageDao = messageDao;
  }

  @Override
  public Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId) {
    Set<String> withoutMatches = new HashSet<>();
    for (String interest : interests) {
      UUID matchedSessionId;
      try {
        matchedSessionId =
            queueService.getRandomMemberFromInterest(interest).orElseThrow(RuntimeException::new);
      } catch (RuntimeException e) {
        withoutMatches.add(interest);
        continue;
      }
      if (matchedSessionId.equals(userId)) return Optional.empty();

      queueService.removeUserFromInterests(userId);
      queueService.removeUserFromInterests(matchedSessionId);

      Set<AuthenticatedConversationMember> memberSet =
          this.getSetOfConversationMembers(matchedSessionId, userId);
      Conversation conversation = conversationService.create();
      return Optional.of(new QueueMatchedResponse<>(interest, memberSet, conversation.getId()));
    }

    if (!withoutMatches.isEmpty()) {
      queueService.placeUserInInterestsSet(withoutMatches, userId);
      LOG.info("No match found for interests: {}", withoutMatches);
    }

    return Optional.empty();
  }

  private Set<AuthenticatedConversationMember> getSetOfConversationMembers(
      UUID matchedId, UUID matcherId) {
    User user1 = userService.findUserById(matcherId).orElseThrow(UserNotFoundException::new);
    AuthenticatedConversationMember member1 =
        new AuthenticatedConversationMember(
            user1.getId(), user1.getUsername(), user1.getProfilePic());
    User user2 = userService.findUserById(matchedId).orElseThrow(UserNotFoundException::new);
    AuthenticatedConversationMember member2 =
        new AuthenticatedConversationMember(
            user2.getId(), user2.getUsername(), user2.getProfilePic());
    return new HashSet<>(Set.of(member1, member2));
  }

  @Override
  public MessageAndReceivers chat(UUID conversationId, UUID senderId, String body) {
    Optional<Conversation> conversation = conversationService.getConversation(conversationId);
    if (conversation.isEmpty()) {
      LOG.error("Invalid conversation id.");
      throw new RuntimeException();
    }

    Message message = new Message();
    User user = userService.findUserById(senderId).orElseThrow(UserNotFoundException::new);

    message.setConversation(conversation.get());
    message.setBody(body);
    message.setSender(user);

    messageDao.save(message);

    ChatMessage chatMessage = new ChatMessage(user.getId(), body);

    Set<UUID> receivers =
        conversation.get().getMembers().stream().map(User::getId).collect(Collectors.toSet());
    return new MessageAndReceivers(chatMessage, receivers);
  }
}
