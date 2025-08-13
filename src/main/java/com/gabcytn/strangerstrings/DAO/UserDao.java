package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.Entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);
}
