package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.Service.UserService;
import java.security.Principal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class TopicSubscriptionInterceptor implements ChannelInterceptor {
  private static final Logger LOG = LoggerFactory.getLogger(TopicSubscriptionInterceptor.class);
  private final UserService userService;

  public TopicSubscriptionInterceptor(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
    if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
      Principal userPrincipal = headerAccessor.getUser();
      if (!validateSubscription(userPrincipal, headerAccessor.getDestination())) {
        LOG.info("Invalid credentials. Rejecting subscription...");
        throw new MessagingException("No permission for this topic.");
      }
    }
    return message;
  }

  private boolean validateSubscription(Principal principal, String topicDestination) {
    if (principal == null) {
      LOG.info("Principal is null.");
      return false;
    }

    boolean isUserAuthenticated = userService.userExistsById(UUID.fromString(principal.getName()));
    boolean isChannelAuthenticated = topicDestination.contains("authenticated");

    if (!topicDestination.startsWith("/user")) return true;

    if (isUserAuthenticated) return true;
    else return !isChannelAuthenticated;
  }
}
