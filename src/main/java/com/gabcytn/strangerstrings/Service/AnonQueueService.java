package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Cache.UsersInterestDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier(value = "AnonQueueService")
public class AnonQueueService extends AbstractQueueService {
  protected AnonQueueService(
      RedisTemplate<String, Object> redisTemplate, UsersInterestDao usersInterestDao) {
    super("anon:", redisTemplate, usersInterestDao);
  }
}
