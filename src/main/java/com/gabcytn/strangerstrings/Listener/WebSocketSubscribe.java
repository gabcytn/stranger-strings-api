package com.gabcytn.strangerstrings.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketSubscribe {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketSubscribe.class);
  private final SimpMessagingTemplate simpMessagingTemplate;

  public WebSocketSubscribe(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @EventListener
  public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
    String initialDestination = headerAccessor.getDestination();

    LOG.info("Destination: {}", initialDestination);
    assert initialDestination != null;

    assert event.getUser() != null;
    String name = event.getUser().getName();
    LOG.info("Send to user name: {}", name);

    String destination = initialDestination;
    if (destination.startsWith("/user/")) {
      destination = destination.substring(5);
    }
    simpMessagingTemplate.convertAndSendToUser(
        name, destination, "Successfully subscribed to: " + initialDestination);
  }
}
