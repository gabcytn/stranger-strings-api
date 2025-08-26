package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public Optional<User> findUserById(UUID userId) {
    return userDao.findById(userId);
  }

  public Optional<User> findUserByUsername(String username) {
    return userDao.findByUsername(username);
  }

  public Set<User> getUserSetFromIdList(List<UUID> ids) {
    Set<User> userSet = new HashSet<>();
    ids.forEach(
        id -> {
          User user = userDao.findById(id).orElseThrow(UserNotFoundException::new);
          userSet.add(user);
        });

    return userSet;
  }
}
