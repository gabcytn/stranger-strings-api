package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.Service.Interface.QueueService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

// TODO: implement methods
@Service
public class QueueServiceImpl implements QueueService {
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
}
