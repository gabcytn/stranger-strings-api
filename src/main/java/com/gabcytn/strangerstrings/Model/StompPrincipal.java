package com.gabcytn.strangerstrings.Model;

import java.security.Principal;
import java.util.UUID;

public class StompPrincipal implements Principal {
  private final String name;

  public StompPrincipal(UUID name) {
    this.name = name.toString();
  }

  @Override
  public String getName() {
    return name;
  }
}
