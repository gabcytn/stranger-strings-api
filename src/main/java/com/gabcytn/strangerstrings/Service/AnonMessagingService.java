package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Redis.AnonymousChatRoomDao;
import com.gabcytn.strangerstrings.DTO.ChatMessage;
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
@Qualifier("AnonMessagingService")
public class AnonMessagingService implements MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AnonMessagingService.class);
  private final QueueService queueService;
  private final AnonymousChatRoomDao anonymousChatRoomDao;

  public AnonMessagingService(
      @Qualifier("AnonQueueService") QueueService queueService,
      AnonymousChatRoomDao anonymousChatRoomDao) {
    this.queueService = queueService;
    this.anonymousChatRoomDao = anonymousChatRoomDao;
  }

  @Override
  public Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId) {
    Set<String> withoutMatches = new HashSet<>();
    for (String interest : interests) {
      Optional<UUID> member = queueService.getRandomMemberFromInterest(interest);
      if (member.isEmpty()) {
        withoutMatches.add(interest);
        continue;
      }
      UUID matchedSessionId = member.get();

      queueService.removeUserFromInterests(userId);
      queueService.removeUserFromInterests(matchedSessionId);

      ConversationMember member1 = new ConversationMember(matchedSessionId);
      ConversationMember member2 = new ConversationMember(userId);
      Set<ConversationMember> memberSet = new HashSet<>(Set.of(member1, member2));
      UUID conversationId = UUID.randomUUID();
      anonymousChatRoomDao.save(new AnonymousChatRoom(conversationId, memberSet));
      return Optional.of(new QueueMatchedResponse<>(interest, memberSet, conversationId));
    }

    if (!withoutMatches.isEmpty()) {
      queueService.placeUserInInterestsSet(withoutMatches, userId);
      LOG.info("No match found for interests: {}", withoutMatches);
    }

    return Optional.empty();
  }

  @Override
  public MessageAndReceivers chat(UUID conversationId, UUID senderId, String body) {
    Optional<AnonymousChatRoom> chatRoom = anonymousChatRoomDao.findById(conversationId);
    if (chatRoom.isEmpty()) {
      LOG.error("Conversation id invalid.");
      throw new RuntimeException("Invalid conversation id.");
    }
    AnonymousChatRoom conversation = chatRoom.get();
    List<ChatMessage> messages = conversation.getMessages();

    Optional<ConversationMember> conversationMember =
        conversation.getParticipants().stream().filter(p -> p.getId().equals(senderId)).findFirst();
    if (conversationMember.isEmpty()) {
      LOG.error("Sender might not be a member of the conversation.");
      throw new RuntimeException("Sender not a member of the conversation.");
    }

    ChatMessage chatMessage = new ChatMessage(senderId, body);
    messages.add(chatMessage);

    conversation.setMessages(messages);
    anonymousChatRoomDao.save(conversation);

    Set<UUID> receivers =
        chatRoom.get().getParticipants().stream()
            .map(ConversationMember::getId)
            .collect(Collectors.toSet());
    return new MessageAndReceivers(chatMessage, receivers);
  }
}
