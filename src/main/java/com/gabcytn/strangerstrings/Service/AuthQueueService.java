package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Redis.UsersInterestDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier(value = "AuthQueueService")
public class AuthQueueService extends AbstractQueueService {
  protected AuthQueueService(
      RedisTemplate<String, Object> redisTemplate, UsersInterestDao usersInterestDao) {
    super("authInterestSet:", redisTemplate, usersInterestDao);
  }
}
