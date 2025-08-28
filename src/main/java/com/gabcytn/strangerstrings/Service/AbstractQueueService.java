package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

// TODO: implementation
// TODO: get api key as prefix.
public abstract class AbstractQueueService implements QueueService {
  private final String prefixKey;

  protected AbstractQueueService(String prefixKey) {
    this.prefixKey = prefixKey;
  }

  // concrete classes shall use this constructor
  protected AbstractQueueService(String apiKey, String prefixKey) {
    this.prefixKey = prefixKey + apiKey;
  }

  protected String namespaced(String interest) {
    return prefixKey + interest;
  }

  @Override
  public void removeUserFromInterests(UUID userId) {}

  @Override
  public Optional<UUID> getRandomMemberFromInterest(String interest) {
    return Optional.empty();
  }

  @Override
  public boolean isInterestSetEmpty(String interest) {
    return false;
  }

  @Override
  public void placeUserInInterestsSet(List<String> interests, UUID userId) {}

  @Override
  public void placeUserInInterestsSet(String interest, UUID userId) {}

  @Override
  public Optional<Set<String>> findInterestsByUserId(UUID userId) {
    return Optional.empty();
  }
}
