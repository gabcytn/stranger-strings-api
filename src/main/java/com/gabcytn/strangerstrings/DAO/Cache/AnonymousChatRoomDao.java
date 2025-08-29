package com.gabcytn.strangerstrings.DAO.Cache;

import com.gabcytn.strangerstrings.Model.AnonymousChatRoom;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymousChatRoomDao extends CrudRepository<AnonymousChatRoom, UUID> {}
