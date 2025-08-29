package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.ConversationDao;
import com.gabcytn.strangerstrings.Entity.Conversation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
  private final ConversationDao conversationDao;

  public ConversationService(ConversationDao conversationDao) {
    this.conversationDao = conversationDao;
  }

  public Conversation create() {
    Conversation conversation = new Conversation();
    return conversationDao.save(conversation);
  }

  public Optional<Conversation> getConversation(UUID id) {
    return conversationDao.findById(id);
  }

  public void save(Conversation conversation) {
    conversationDao.save(conversation);
  }
}
