package com.gabcytn.shortnotice.DAO;

import com.gabcytn.shortnotice.Entity.Conversation;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationDao extends CrudRepository<Conversation, UUID> {}
