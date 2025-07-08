package com.gabcytn.shortnotice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserLoginDTO {
  @NotNull(message = "Username is required.")
  @NotBlank(message = "Username must not be blank")
  private String username;

  @NotNull(message = "Password is required.")
  @NotBlank(message = "Password must not be blank")
  private String password;

  public UserLoginDTO(String username, String password) {
    this.username = username;
    this.password = password;
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
}
