package com.gabcytn.shortnotice.Listener;

import java.util.Objects;
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
    String destination = headerAccessor.getDestination();

    LOG.info("Destination: {}", destination);
    assert destination != null;

    String name = Objects.requireNonNull(headerAccessor.getUser()).getName();
    LOG.info("Send to user name: {}", name);
    simpMessagingTemplate.convertAndSendToUser(
        name, "/topic/anonymous/queue", "Successfully subscribed to: " + destination);
  }
}
