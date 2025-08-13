package com.gabcytn.shortnotice.Model;

import java.util.UUID;

public class RedisAnonymousMessage {
  private final UUID senderId;
  private final String body;

  private RedisAnonymousMessage(UUID senderId, String body) {
    this.senderId = senderId;
    this.body = body;
  }

  public static RedisAnonymousMessage of(UUID senderId, String body) {
    return new RedisAnonymousMessage(senderId, body);
  }

  public UUID getSenderId() {
    return senderId;
  }

  public String getBody() {
    return body;
  }
}
