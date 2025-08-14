package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.DTO.StompPrincipal;
import com.gabcytn.strangerstrings.DTO.UserPrincipal;
import com.gabcytn.strangerstrings.Entity.User;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
  private static final Logger LOG = LoggerFactory.getLogger(CustomHandshakeHandler.class);
  private final UserDao userDao;

  public CustomHandshakeHandler(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  protected Principal determineUser(
      ServerHttpRequest request, WebSocketHandler webSocketHandler, Map<String, Object> map) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    try {
      if (authentication == null || !authentication.isAuthenticated()) throw new RuntimeException();

      UUID userId = getUserId(authentication).orElseThrow(RuntimeException::new);
      LOG.info("Retrieved user id in handshake: {}", userId);
      return new StompPrincipal(userId);
    } catch (RuntimeException e) {
      LOG.info("No retrieved user; user is anonymous");
      return new StompPrincipal(UUID.randomUUID());
    }
  }

  private Optional<UUID> getUserId(Authentication authentication) {
    if (authentication.getPrincipal() instanceof UserPrincipal principal) {
      String username = principal.getUsername();
      LOG.info("Username from principal: {}", username);
      Optional<User> optionalUser = userDao.findByUsername(username);

      // returns empty if user doesn't exist, returns id if it does
      return optionalUser.map(User::getId);
    }

    return Optional.empty();
  }
}
