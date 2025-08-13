package com.gabcytn.strangerstrings.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDto
{
  @NotNull(message = "Username is required.")
  @NotBlank(message = "Username must not be blank")
  private String username;

  @NotNull(message = "Password is required.")
  @NotBlank(message = "Password must not be blank")
  private String password;

  @NotNull(message = "Device name is required.")
  @NotBlank(message = "Device name must not be blank")
  private String deviceName;

  public LoginRequestDto(String username, String password, String deviceName) {
    this.username = username;
    this.password = password;
    this.deviceName = deviceName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }
}
