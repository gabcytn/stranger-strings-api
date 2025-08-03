package com.gabcytn.shortnotice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
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
        .simpTypeMatchers(
            SimpMessageType.DISCONNECT,
            SimpMessageType.CONNECT,
            SimpMessageType.SUBSCRIBE,
            SimpMessageType.UNSUBSCRIBE,
            SimpMessageType.OTHER)
        .permitAll()
        .anyMessage()
        .authenticated()
        .build();
  }
}
