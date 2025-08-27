package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.DAO.UserDao;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final CustomHandshakeHandler handshakeHandler;
  private final UserDao userDao;

  public WebSocketConfig(CustomHandshakeHandler handshakeHandler, UserDao userDao) {
    this.handshakeHandler = handshakeHandler;
    this.userDao = userDao;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws/v1/stranger-strings")
        .setAllowedOriginPatterns("*")
        .setHandshakeHandler(handshakeHandler);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new TopicSubscriptionInterceptor(userDao));
  }
}
