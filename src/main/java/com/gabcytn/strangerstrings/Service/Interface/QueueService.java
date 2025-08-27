package com.gabcytn.strangerstrings.Service.Interface;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueService {
  void removeUserFromInterests(UUID userId);

  Optional<UUID> getRandomMemberFromInterest(String interest);

  boolean isInterestSetEmpty(String interest);

  void placeUserInInterestsSet(List<String> interests, UUID userId);

  void placeUserInInterestsSet(String interest, UUID userId);
}
