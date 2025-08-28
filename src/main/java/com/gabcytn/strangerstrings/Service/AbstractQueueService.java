package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Cache.UsersInterestDao;
import com.gabcytn.strangerstrings.Model.UsersInterest;
import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

// TODO: get api key as prefix.
public abstract class AbstractQueueService implements QueueService {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractQueueService.class);
  private final String prefixKey;
  private final RedisTemplate<String, Object> redisTemplate;
  private final UsersInterestDao usersInterestDao;

  protected AbstractQueueService(
      String prefixKey,
      RedisTemplate<String, Object> redisTemplate,
      UsersInterestDao usersInterestDao) {
    this.prefixKey = prefixKey;
    this.redisTemplate = redisTemplate;
    this.usersInterestDao = usersInterestDao;
  }

  // NOTE: concrete classes shall use this constructor (set up api keys first)
  protected AbstractQueueService(
      String apiKey,
      String prefixKey,
      RedisTemplate<String, Object> redisTemplate,
      UsersInterestDao usersInterestDao) {
    this.redisTemplate = redisTemplate;
    this.usersInterestDao = usersInterestDao;
    this.prefixKey = prefixKey + apiKey;
  }

  protected String namespaced(String interest) {
    return prefixKey + interest;
  }

  @Override
  public void removeUserFromInterests(UUID userId) {
    Optional<Set<String>> interests = this.findInterestsByUserId(userId);
    if (interests.isEmpty()) return;

    interests
        .get()
        .forEach(
            interest -> {
              redisTemplate.opsForSet().remove(this.namespaced(interest), userId);
            });

    usersInterestDao.deleteById(userId);
  }

  @Override
  public Optional<UUID> getRandomMemberFromInterest(String interest) {
    Object randomMemberObject = redisTemplate.opsForSet().randomMember(this.namespaced(interest));
    if (randomMemberObject == null) { // disregard intellij warning, condition is NOT always false.
      LOG.info("OBJECT IS NULL!!!");
			return Optional.empty();
		}
    try {
      return Optional.of(UUID.fromString((String) randomMemberObject));
    } catch (RuntimeException e) {
      LOG.error("Error casting object to UUID.");
      LOG.error("Object retrieved: {}", randomMemberObject);
      return Optional.empty();
    }
  }

  @Override
  public boolean isInterestSetEmpty(String interest) {
    Long size = redisTemplate.opsForSet().size(this.namespaced(interest));
    if (size == null) {
      LOG.debug("Interest size is null.");
      return false;
    }
    LOG.info("Interest set size: {}", size);
    return size <= 0;
  }

  @Override
  public void placeUserInInterestsSet(Set<String> interests, UUID userId) {
    interests.forEach(
        interest -> {
          redisTemplate.opsForSet().add(this.namespaced(interest), userId);
        });

    usersInterestDao.save(new UsersInterest(userId, interests));
  }

  @Override
  public void placeUserInInterestsSet(String interest, UUID userId) {
    redisTemplate.opsForSet().add(this.namespaced(interest), userId);
  }

  @Override
  public Optional<Set<String>> findInterestsByUserId(UUID userId) {
    Optional<UsersInterest> usersInterest = usersInterestDao.findById(userId);
    if (usersInterest.isEmpty()) {
      LOG.debug("Users -> Interests map not found.");
      return Optional.empty();
    }
    return Optional.of(usersInterest.get().getInterests());
  }
}
