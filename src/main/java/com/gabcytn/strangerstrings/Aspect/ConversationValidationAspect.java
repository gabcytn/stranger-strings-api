package com.gabcytn.strangerstrings.Aspect;

import com.gabcytn.strangerstrings.DAO.Redis.AnonymousChatRoomDao;
import com.gabcytn.strangerstrings.DAO.Redis.UsersInterestDao;
import com.gabcytn.strangerstrings.DAO.ConversationDao;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.DTO.UserPrincipal;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Exception.NonConversationMemberException;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import com.gabcytn.strangerstrings.Model.AnonymousChatRoom;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Service.ConversationService;
import com.gabcytn.strangerstrings.Service.UserService;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConversationValidationAspect {
  private static final Logger LOG = LoggerFactory.getLogger(ConversationValidationAspect.class);
  private final UserService userService;
  private final ConversationDao conversationDao;
  private final UsersInterestDao usersInterestDao;
  private final AnonymousChatRoomDao anonymousChatRoomDao;
  private final ConversationService conversationService;

  public ConversationValidationAspect(
      UserService userService,
      ConversationDao conversationDao,
      UsersInterestDao usersInterestDao,
      AnonymousChatRoomDao anonymousChatRoomDao,
      ConversationService conversationService) {
    this.userService = userService;
    this.conversationDao = conversationDao;
    this.usersInterestDao = usersInterestDao;
    this.anonymousChatRoomDao = anonymousChatRoomDao;
    this.conversationService = conversationService;
  }

  @Around(
      "execution(* com.gabcytn.strangerstrings.Controller.WebSocketMessagingController.anonymousMessage(..))")
  public Object validateIncomingAnonymousChatMessage(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    if (args[0] instanceof StompSendPayload payload && args[1] instanceof Principal principal) {
      Optional<AnonymousChatRoom> nullableChatRoom =
          anonymousChatRoomDao.findById(payload.getConversationId());
      if (nullableChatRoom.isEmpty()) return null;
      AnonymousChatRoom chatRoom = nullableChatRoom.get();
      Set<ConversationMember> members = chatRoom.getParticipants();
      Optional<ConversationMember> member =
          members.stream()
              .filter(m -> m.getId().equals(UUID.fromString(principal.getName())))
              .findFirst();

      if (member.isPresent()) {
        return pjp.proceed();
      }

      this.log(payload.getConversationId(), UUID.fromString(principal.getName()));
    }

    return null;
  }

  @Around(
      "execution(* com.gabcytn.strangerstrings.Controller.WebSocketMessagingController.authenticatedMessage(..))")
  public Object validateIncomingAuthenticatedChatMessage(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    if (args[0] instanceof StompSendPayload payload && args[1] instanceof Principal principal) {
      Optional<Conversation> nullableConversation =
          conversationService.getConversation(payload.getConversationId());
      if (nullableConversation.isEmpty()) return null;
      Conversation conversation = nullableConversation.get();
      Set<User> members = conversation.getMembers();
      Optional<User> user =
          members.stream()
              .filter(m -> m.getId().equals(UUID.fromString(principal.getName())))
              .findFirst();
      if (user.isPresent()) {
        return pjp.proceed();
      }

      this.log(payload.getConversationId(), UUID.fromString(principal.getName()));
    }

    return null;
  }

  private void log(UUID conversationId, UUID userId) {
    LOG.warn("Conversation id: {}", conversationId);
    LOG.warn("User: {}", userId);
  }

  @Around("execution(* com.gabcytn.strangerstrings.Controller.MessageRestController.get(..))")
  public Object validate(ProceedingJoinPoint pjp) throws Throwable {
    if (pjp.getArgs()[0] instanceof UUID conversationId) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      User user =
          userService
              .findUserByUsername(((UserPrincipal) authentication.getPrincipal()).getUsername())
              .orElseThrow(UserNotFoundException::new);
      Set<User> members =
          conversationDao
              .findById(conversationId)
              .orElseThrow(ConversationNotFoundException::new)
              .getMembers();
      Optional<User> member =
          members.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
      if (member.isPresent()) {
        return pjp.proceed();
      }
      throw new NonConversationMemberException("User is not a member of the conversation");
    }

    return null;
  }

  @Around("@annotation(com.gabcytn.strangerstrings.Aspect.Annotation.NoDuplicateRequest)")
  public Object validateNoDuplicateMatchRequest(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    Principal principal;
    try {
      principal = (Principal) args[1];
    } catch (RuntimeException e) {
      LOG.info("Error casting args[1] to java.security.Principal");
      return null;
    }
    boolean isDuplicate = usersInterestDao.existsById(UUID.fromString(principal.getName()));
    LOG.info("Is duplicate: {}", isDuplicate);

    return isDuplicate ? null : pjp.proceed(args);
  }
}
