package com.gabcytn.shortnotice.QueueDAO;

import com.gabcytn.shortnotice.DTO.ChatQueueData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedisQueueDao extends CrudRepository<ChatQueueData, String> {
	Optional<List<ChatQueueData>> findByValueContainingIgnoreCase(String value);
}
