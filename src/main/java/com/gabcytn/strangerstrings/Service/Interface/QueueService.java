package com.gabcytn.strangerstrings.Service.Interface;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing user membership in interest-based queues.
 * <p>
 * Each queue is identified by an {@code interest}. Implementations may
 * apply namespacing (e.g., based on authentication type or client API key).
 * </p>
 */
public interface QueueService {

  /**
   * Removes the given user from all interests they are currently part of.
   *
   * @param userId the unique identifier of the user to remove
   */
  void removeUserFromInterests(UUID userId);

  /**
   * Retrieves a random user from the queue of the given interest.
   *
   * @param interest the interest queue to pull from
   * @return an {@link Optional} containing a randomly selected user ID,
   *         or {@link Optional#empty()} if the interest has no members
   */
  Optional<UUID> getRandomMemberFromInterest(String interest);

  /**
   * Checks whether the queue for the given interest contains any members.
   *
   * @param interest the interest queue to check
   * @return {@code true} if the queue is empty, {@code false} otherwise
   */
  boolean isInterestSetEmpty(String interest);

  /**
   * Adds the given user to all the specified interest queues.
   *
   * @param interests list of interests to add the user to
   * @param userId    the unique identifier of the user
   */
  void placeUserInInterestsSet(List<String> interests, UUID userId);

  /**
   * Adds the given user to a single interest queue.
   *
   * @param interest the interest to add the user to
   * @param userId   the unique identifier of the user
   */
  void placeUserInInterestsSet(String interest, UUID userId);

  /**
   * Finds all interests that the given user is currently a member of.
   *
   * @param userId the unique identifier of the user
   * @return an {@link Optional} containing the set of interest names,
   *         or {@link Optional#empty()} if the user belongs to none
   */
  Optional<Set<String>> findInterestsByUserId(UUID userId);
}
