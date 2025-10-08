package com.gabcytn.strangerstrings.Service.Interface;

import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.MessageAndReceivers;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessagingService {
  Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId);

  MessageAndReceivers chat(UUID conversationId, UUID senderId, String body);

  void disconnectFromQueue(UUID userId);
  Set<UUID> disconnectFromChat(UUID userId, UUID conversationId);
}
