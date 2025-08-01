package com.gabcytn.shortnotice.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ChatInitiationDto {
  @NotNull(message = "Interests field is required.")
  private List<String> interests;

  public List<String> getInterests() {
    return interests;
  }

  public void setInterests(List<String> interests) {
    this.interests = interests;
  }

  @Override
  public String toString() {
    return "ChatInitiationDto{" + "interests=" + interests + '}';
  }
}
