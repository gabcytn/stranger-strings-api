package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Cache.AnonymousChatRoomDao;
import com.gabcytn.strangerstrings.Model.AnonymousChatMessage;
import com.gabcytn.strangerstrings.Model.AnonymousChatRoom;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("AnonMessagingService")
public class AnonymousMessagingService implements MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingService.class);
  private final QueueService queueService;
  private final AnonymousChatRoomDao anonymousChatRoomDao;

  public AnonymousMessagingService(
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
  public void chat(UUID conversationId, UUID senderId, String body) {
    Optional<AnonymousChatRoom> chatRoom = anonymousChatRoomDao.findById(conversationId);
    if (chatRoom.isEmpty()) {
      LOG.error("Conversation id invalid.");
      throw new RuntimeException("Invalid conversation id.");
    }
    AnonymousChatRoom conversation = chatRoom.get();
    List<AnonymousChatMessage> messages = conversation.getMessages();

    Optional<ConversationMember> conversationMember =
            conversation.getParticipants().stream()
                    .filter(p -> p.getId().equals(senderId))
                    .findFirst();

    conversationMember.ifPresent(p -> {
      AnonymousChatMessage chatMessage = new AnonymousChatMessage(p, body);
      messages.add(chatMessage);

      conversation.setMessages(messages);
      anonymousChatRoomDao.save(conversation);

    });

    // TODO: send message to members
  }
}
