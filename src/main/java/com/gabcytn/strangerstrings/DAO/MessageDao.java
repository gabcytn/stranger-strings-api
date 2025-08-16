package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.Entity.Message;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDao extends ListCrudRepository<Message, Integer> {}
