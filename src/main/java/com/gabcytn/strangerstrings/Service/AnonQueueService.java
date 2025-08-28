package com.gabcytn.strangerstrings.Service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier(value = "AnonQueueService")
public class AnonQueueService extends AbstractQueueService {
  public AnonQueueService() {
    super("anon:");
  }
}