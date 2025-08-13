package com.gabcytn.strangerstrings.Config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import com.gabcytn.strangerstrings.DTO.StompPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
  @Override
  protected Principal determineUser(
      ServerHttpRequest request, WebSocketHandler webSocketHandler, Map<String, Object> map) {
    return new StompPrincipal(UUID.randomUUID().toString());
  }
}
