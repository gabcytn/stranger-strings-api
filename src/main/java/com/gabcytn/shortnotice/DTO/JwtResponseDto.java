package com.gabcytn.shortnotice.DTO;

public class JwtResponseDto
{
  private String token;
  private long expiresIn;

  public JwtResponseDto(String token, long expiresIn) {
    this.token = token;
    this.expiresIn = expiresIn;
  }

  public JwtResponseDto() {}

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  @Override
  public String toString() {
    return "LoginResponseDto{" + "token='" + token + '\'' + ", expiresIn=" + expiresIn + '}';
  }
}
