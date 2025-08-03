package com.gabcytn.shortnotice.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
  public RedisTemplate<Object, Object> redisQueueTemplate(
      @Qualifier(value = "queueRedisConnectionFactory")
          LettuceConnectionFactory lettuceConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();

    template.setConnectionFactory(lettuceConnectionFactory);
    template.setKeySerializer(StringRedisSerializer.UTF_8);

    GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(redisSerializer);
    template.setHashKeySerializer(redisSerializer);
    template.setHashValueSerializer(redisSerializer);
    template.afterPropertiesSet();

    return template;
  }
}
