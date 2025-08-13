package com.gabcytn.strangerstrings.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabcytn.strangerstrings.DAO.RedisCacheDao;
import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.DTO.CacheData;
import com.gabcytn.strangerstrings.DTO.UserPrincipal;
import com.gabcytn.strangerstrings.Entity.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceAuth implements UserDetailsService {
  private final UserDao userDAO;
  private final RedisCacheDao redisCacheDao;
  private final ObjectMapper objectMapper;

  public UserDetailsServiceAuth(
          UserDao userDAO, RedisCacheDao redisCacheDao, ObjectMapper objectMapper) {
    this.userDAO = userDAO;
    this.redisCacheDao = redisCacheDao;
    this.objectMapper = objectMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<CacheData> cacheData = redisCacheDao.findById(username);
    if (cacheData.isPresent()) {
      System.out.println("Cache hit!");
      TypeReference<User> mapType = new TypeReference<>() {};
      try {
        User cachedUser = objectMapper.readValue(cacheData.get().getValue(), mapType);
        return new UserPrincipal(cachedUser);
      } catch (JsonProcessingException e) {
        System.err.println("Error mapping cached string value to user entity");
        throw new UsernameNotFoundException("Error mapping cached string value to user entity");
      }
    }

    System.out.println("Cache miss!");
    Optional<User> user = userDAO.findByUsername(username);
    if (user.isPresent()) {
      User presentUser = user.get();
      try {
        redisCacheDao.save(
            new CacheData(
                presentUser.getEmail(),
                objectMapper.writeValueAsString(presentUser),
                60L * 15)); // cache for 15 mins
        return new UserPrincipal(presentUser);
      } catch (JsonProcessingException e) {
        System.err.println("Error writing user entity as String");
        throw new UsernameNotFoundException("Error writing user entity as String");
      }
    }

    System.err.println("Username/Email not found.");
    throw new UsernameNotFoundException("Username/Email not found.");
  }
}
