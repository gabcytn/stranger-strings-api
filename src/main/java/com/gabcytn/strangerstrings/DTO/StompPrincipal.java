package com.gabcytn.strangerstrings.DTO;

import java.security.Principal;
import java.util.UUID;

public class StompPrincipal implements Principal {
  private final String name;

  public StompPrincipal(UUID identifier) {
    this.name = identifier.toString();
  }

  @Override
  public String getName() {
    return name;
  }
}
