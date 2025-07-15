package com.gabcytn.shortnotice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RefreshTokenRequestDto {
  @NotNull(message = "Device name is required.")
  @NotBlank(message = "Device name must not be blank.")
  private String deviceName;

  public RefreshTokenRequestDto(String deviceName) {
    this.deviceName = deviceName;
  }

  public RefreshTokenRequestDto() {}

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  @Override
  public String toString() {
    return "RefreshTokenRequestDto{" + "deviceName='" + deviceName + '\'' + '}';
  }
}
