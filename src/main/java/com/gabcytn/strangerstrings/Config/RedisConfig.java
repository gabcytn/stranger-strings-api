package com.gabcytn.strangerstrings.Config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.gabcytn.strangerstrings.DAO.Cache")
public class RedisConfig {
  @Bean
  @Primary
  public RedisProperties redisProperties() {
    return new RedisProperties();
  }

  @Bean
  public LettuceConnectionFactory queueRedisConnectionFactory(RedisProperties redisProperties) {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

    configuration.setHostName(redisProperties.getHost());
    configuration.setPort(redisProperties.getPort());
    configuration.setDatabase(0);

    return new LettuceConnectionFactory(configuration);
  }

  //  @Bean
  //  public RedisTemplate<String, Object> redisQueueTemplate(
  //      LettuceConnectionFactory lettuceConnectionFactory) {
  //    RedisTemplate<String, Object> template = new RedisTemplate<>();
  //
  //    template.setConnectionFactory(lettuceConnectionFactory);
  //    template.setKeySerializer(StringRedisSerializer.UTF_8);
  //
  //    GenericJackson2JsonRedisSerializer redisSerializer = new
  // GenericJackson2JsonRedisSerializer();
  //    template.setValueSerializer(redisSerializer);
  //    template.setHashKeySerializer(StringRedisSerializer.UTF_8);
  //    template.setHashValueSerializer(
  //        new Jackson2JsonRedisSerializer<>(new TypeReference<List<String>>() {}.getClass()));
  //    template.afterPropertiesSet();
  //
  //    return template;
  //  }
  //
  //  @Bean
  //  public RedisTemplate<String, Map<String, List<String>>> redisUsersInterestsMapTemplate(
  //      LettuceConnectionFactory lettuceConnectionFactory) {
  //    RedisTemplate<String, Map<String, List<String>>> template = new RedisTemplate<>();
  //
  //    template.setConnectionFactory(lettuceConnectionFactory);
  //    template.setKeySerializer(StringRedisSerializer.UTF_8);
  //    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
  //    template.afterPropertiesSet();
  //
  //    return template;
  //  }
  //
  //  @Bean
  //  public RedisTemplate<String, RefreshTokenValidator> redisRefreshTokenTemplate(
  //      LettuceConnectionFactory lettuceConnectionFactory) {
  //    RedisTemplate<String, RefreshTokenValidator> template = new RedisTemplate<>();
  //
  //    template.setConnectionFactory(lettuceConnectionFactory);
  //    template.setKeySerializer(StringRedisSerializer.UTF_8);
  //    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
  //    template.afterPropertiesSet();
  //
  //    return template;
  //  }
  //
  //  @Bean
  //  public RedisTemplate<String, UserInterestsMap> redisUsersToInterestsMapTemplate(
  //      LettuceConnectionFactory connectionFactory) {
  //    RedisTemplate<String, UserInterestsMap> template = new RedisTemplate<>();
  //    template.setConnectionFactory(connectionFactory);
  //    template.setKeySerializer(StringRedisSerializer.UTF_8);
  //    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
  //    template.afterPropertiesSet();
  //
  //    return template;
  //  }
}
