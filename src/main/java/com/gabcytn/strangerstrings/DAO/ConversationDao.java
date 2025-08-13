package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.Entity.Conversation;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationDao extends CrudRepository<Conversation, UUID> {}
