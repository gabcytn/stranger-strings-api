package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;

public interface MessageStorageService {
  void save(String message, User sender, Conversation conversation);
}
