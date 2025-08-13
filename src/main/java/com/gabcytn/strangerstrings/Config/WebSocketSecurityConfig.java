package com.gabcytn.strangerstrings.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {
  @Bean
  AuthorizationManager<Message<?>> messageAuthorizationManager(
      MessageMatcherDelegatingAuthorizationManager.Builder messages) {

    return messages
        .simpDestMatchers("/app/anonymous/**")
        .permitAll()
        .simpSubscribeDestMatchers("/topic/anonymous/**")
        .permitAll()
        .simpMessageDestMatchers("/topic/anonymous/**")
        .permitAll()
        .simpTypeMatchers(
            SimpMessageType.CONNECT_ACK,
            SimpMessageType.DISCONNECT,
            SimpMessageType.CONNECT,
            SimpMessageType.SUBSCRIBE,
            SimpMessageType.UNSUBSCRIBE,
            SimpMessageType.MESSAGE,
            SimpMessageType.OTHER)
        .permitAll()
        .anyMessage()
        .authenticated()
        .build();
  }

  // NOTE: this 'temporarily' turns off CSRF for CONNECT STOMP frames as there's no support for it
  // source: https://stackoverflow.com/a/77025523/28779183
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Bean
  public ChannelInterceptor csrfChannelInterceptor() {
    return new ChannelInterceptor() {};
  }
}
