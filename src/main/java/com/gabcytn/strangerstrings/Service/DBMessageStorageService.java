package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class DBMessageStorageService implements MessageStorageService {
  private final MessageDao messageDao;

  public DBMessageStorageService(MessageDao messageDao) {
    this.messageDao = messageDao;
  }

  @Override
  public void save(String message, User sender, Conversation conversation) {
    Message messageEntity = new Message();
    messageEntity.setBody(message);
    messageEntity.setConversation(conversation);
    messageEntity.setSender(sender);
    messageDao.save(messageEntity);
  }
}
