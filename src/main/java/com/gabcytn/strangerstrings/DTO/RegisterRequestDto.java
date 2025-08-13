package com.gabcytn.strangerstrings.DTO;

import com.gabcytn.strangerstrings.Validation.PasswordsMatch;
import com.gabcytn.strangerstrings.Validation.UniqueEmail;
import com.gabcytn.strangerstrings.Validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@PasswordsMatch
public class RegisterRequestDto
{
  @NotNull(message = "Email is required.")
  @Email
  @UniqueEmail(message = "Email already taken.")
  private String email;

  @NotNull(message = "Username is required.")
  @UniqueUsername(message = "Username already taken.")
  @Size(min = 6, max = 64, message = "Username must be between 8 and 64 characters long.")
  private String username;

  @NotNull(message = "Password is required.")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long.")
  private String password;

  @NotNull(message = "Confirm password is required.")
  private String confirmPassword;

  public RegisterRequestDto(String email, String username, String password, String confirmPassword) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.confirmPassword = confirmPassword;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }
}
