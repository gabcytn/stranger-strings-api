package com.gabcytn.shortnotice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class ShortnoticeApplication {
  private static String USERS_TO_INTERESTS_MAP_REDIS_KEY;

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(ShortnoticeApplication.class, args);

    @SuppressWarnings("unchecked")
    RedisTemplate<String, Map<String, List<String>>> redisTemplate =
        context.getBean("redisUsersInterestsMapTemplate", RedisTemplate.class);

    // initialize users-list(interests) map
    Map<String, List<String>> map = new HashMap<>();
    map.put("_init", List.of());
    redisTemplate.opsForValue().set(USERS_TO_INTERESTS_MAP_REDIS_KEY, map);
  }

  @Value("${spring.data.redis.users-interests-map}")
  public void setIsUserMatchedMapKey(String key) {
    USERS_TO_INTERESTS_MAP_REDIS_KEY = key;
  }
}
