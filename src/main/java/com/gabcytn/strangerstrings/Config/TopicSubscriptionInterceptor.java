package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.Service.UserService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class TopicSubscriptionInterceptor implements ChannelInterceptor {
  private final UserService userService;

  public TopicSubscriptionInterceptor(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
    Principal userPrincipal = headerAccessor.getUser();
    if (userPrincipal == null || headerAccessor.getDestination() == null)
      throw new MessagingException("Principal/Destination is null.");
    if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
      if (!validateSubscription(userPrincipal, headerAccessor.getDestination())) {
        throw new MessagingException("No permission for this topic.");
      }
    } else if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
      if (!validateSend(userPrincipal, headerAccessor.getDestination())) {
        throw new MessagingException("User is not allowed to send in this destination.");
      }
    }

    return message;
  }

  private boolean validateSubscription(Principal principal, String topicDestination) {
    boolean isUserAuthenticated = userService.userExistsById(UUID.fromString(principal.getName()));
    boolean isChannelAuthenticated = topicDestination.contains("authenticated");

    if (!topicDestination.startsWith("/user")) return true;

    if (isUserAuthenticated) return true;
    else return !isChannelAuthenticated;
  }

  private boolean validateSend(Principal principal, String topicDestination) {
    boolean isUserAuthenticated = userService.userExistsById(UUID.fromString(principal.getName()));
    return !"/app/authenticated/matcher".equals(topicDestination) || isUserAuthenticated;
  }
}
