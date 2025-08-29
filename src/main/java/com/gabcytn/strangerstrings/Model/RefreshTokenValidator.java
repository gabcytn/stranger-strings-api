package com.gabcytn.strangerstrings.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refreshToken")
public class RefreshTokenValidator {
  @TimeToLive private long expiresAt = 60L * 60 * 24 * 7;
  @Id private String key;
  private String username;
  private String deviceName;

  public RefreshTokenValidator(String key, String username, String deviceName) {
    this.key = key;
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

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(long expiresAt) {
    this.expiresAt = expiresAt;
  }
}
