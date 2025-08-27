package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.Entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, UUID> {
  @Cacheable(value = "user", key = "#id", unless = "#result == null")
  Optional<User> findById(UUID id);

  @Cacheable(value = "user", key = "#username", unless = "#result == null")
  Optional<User> findByUsername(String username);

  @Cacheable(value = "userExists", key = "#id", unless = "#result == null")
  boolean existsById(UUID id);

  Optional<User> findByEmail(String email);
}
