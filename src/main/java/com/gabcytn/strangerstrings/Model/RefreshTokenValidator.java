package com.gabcytn.strangerstrings.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refreshToken")
public class RefreshTokenValidator {
  @Id private String username;
  private String deviceName;
  @TimeToLive private final long expiresAt = 60L * 60 * 24 * 7;

  public RefreshTokenValidator(String username, String deviceName) {
    this.username = username;
    this.deviceName = deviceName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }
}
