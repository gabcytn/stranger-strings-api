package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.Entity.User;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, UUID> {
  @Cacheable(value = "user", key = "#username")
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}
