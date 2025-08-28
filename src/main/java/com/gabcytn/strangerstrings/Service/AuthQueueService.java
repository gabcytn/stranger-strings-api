package com.gabcytn.strangerstrings.Service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier(value = "AuthQueueService")
public class AuthQueueService extends AbstractQueueService {
  protected AuthQueueService() {
    super("auth:");
  }
}
