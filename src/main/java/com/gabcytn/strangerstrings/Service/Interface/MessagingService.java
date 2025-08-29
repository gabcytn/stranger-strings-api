package com.gabcytn.strangerstrings.Service.Interface;

import com.gabcytn.strangerstrings.Model.ChatMessage;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessagingService<T extends ChatMessage> {
  Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId);

  T chat(UUID conversationId, UUID senderId, String body);
}
