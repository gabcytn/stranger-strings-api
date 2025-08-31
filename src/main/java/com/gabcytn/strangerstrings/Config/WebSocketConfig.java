package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.Service.UserService;
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
  private final UserService userService;

  public WebSocketConfig(CustomHandshakeHandler handshakeHandler, UserService userService) {
    this.handshakeHandler = handshakeHandler;
    this.userService = userService;
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
    registration.interceptors(new TopicSubscriptionInterceptor(userService));
  }
}
