package com.gabcytn.strangerstrings.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisQueueConfig {
  @Bean
  public LettuceConnectionFactory queueRedisConnectionFactory(RedisProperties redisProperties) {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

    configuration.setHostName(redisProperties.getHost());
    configuration.setPort(redisProperties.getPort());
    configuration.setDatabase(1);

    return new LettuceConnectionFactory(configuration);
  }

  @Bean
  public RedisTemplate<String, Object> redisQueueTemplate(
      @Qualifier(value = "queueRedisConnectionFactory")
          LettuceConnectionFactory lettuceConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();

    template.setConnectionFactory(lettuceConnectionFactory);
    template.setKeySerializer(StringRedisSerializer.UTF_8);

    GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(redisSerializer);
    template.setHashKeySerializer(StringRedisSerializer.UTF_8);
    template.setHashValueSerializer(
        new Jackson2JsonRedisSerializer<>(new TypeReference<List<String>>() {}.getClass()));
    template.afterPropertiesSet();

    return template;
  }

  @Bean
  public RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate(
      @Qualifier(value = "queueRedisConnectionFactory")
          LettuceConnectionFactory lettuceConnectionFactory) {
    RedisTemplate<String, Map<String, List<String>>> template = new RedisTemplate<>();

    template.setConnectionFactory(lettuceConnectionFactory);
    template.setKeySerializer(StringRedisSerializer.UTF_8);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.afterPropertiesSet();

    return template;
  }
}
