package com.gabcytn.strangerstrings.Service.Interface;

import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessagingService {
  Optional<QueueMatchedResponse<? extends ConversationMember>> match(
      List<String> interests, UUID userId);

  void chat(Conversation conversation, UUID senderId, String body);
}
