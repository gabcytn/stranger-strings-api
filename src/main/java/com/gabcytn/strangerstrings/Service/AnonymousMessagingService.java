package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Cache.AnonymousChatRoomDao;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Model.AnonymousChatRoom;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnonymousMessagingService implements MessagingService {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingService.class);
  private final QueueService queueService;
  private final AnonymousChatRoomDao anonymousChatRoomDao;

  public AnonymousMessagingService(
      QueueService queueService, AnonymousChatRoomDao anonymousChatRoomDao) {
    this.queueService = queueService;
    this.anonymousChatRoomDao = anonymousChatRoomDao;
  }

  @Override
  public Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId) {
    List<String> withoutMatches = new ArrayList<>();
    for (String interest : interests) {
      if (queueService.isInterestSetEmpty(interest)) {
        withoutMatches.add(interest);
        continue;
      }

      UUID matchedSessionId = queueService.getRandomMemberFromInterest(interest);
      if (matchedSessionId.equals(userId)) return Optional.empty();

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
  public void chat(Conversation conversation, UUID senderId, String body) {}
}
