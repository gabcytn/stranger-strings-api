package com.gabcytn.strangerstrings.Model;

import java.security.Principal;
import java.util.UUID;

public class StompPrincipal implements Principal {
  private final String name;

  private StompPrincipal(String name) {
    this.name = name;
  }

  public static StompPrincipal ofAuthenticated(UUID identifier) {
    String name = "auth:" + identifier.toString();
    return new StompPrincipal(name);
  }

  public static StompPrincipal ofAnonymous(UUID identifier) {
    String name = "anon:" + identifier.toString();
    return new StompPrincipal(name);
  }

  @Override
  public String getName() {
    return name;
  }
}
