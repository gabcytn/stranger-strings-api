package com.gabcytn.strangerstrings.Exception;

import java.util.List;

public class NoInterestMatchException extends RuntimeException {
  private final List<String> interests;

  public NoInterestMatchException(List<String> interests) {
    this.interests = interests;
  }

  public List<String> getInterests() {
    return interests;
  }
}
