package com.gabcytn.strangerstrings.Aspect;

import com.gabcytn.strangerstrings.DAO.Cache.AnonymousConversationDao;
import com.gabcytn.strangerstrings.DAO.ConversationDao;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.DTO.UserPrincipal;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Exception.NonConversationMemberException;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import com.gabcytn.strangerstrings.Model.AnonymousConversation;
import com.gabcytn.strangerstrings.Model.ConversationMemberDetails;
import com.gabcytn.strangerstrings.Service.RedisQueueService;
import com.gabcytn.strangerstrings.Service.UserService;
import java.security.Principal;
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
  private final RedisQueueService redisQueueService;
  private final UserService userService;
  private final AnonymousConversationDao anonymousConversationDao;
  private final ConversationDao conversationDao;

  public ConversationValidationAspect(
      RedisQueueService redisQueueService,
      UserService userService,
      AnonymousConversationDao anonymousConversationDao,
      ConversationDao conversationDao) {
    this.redisQueueService = redisQueueService;
    this.userService = userService;
    this.anonymousConversationDao = anonymousConversationDao;
    this.conversationDao = conversationDao;
  }

  @Around("@annotation(com.gabcytn.strangerstrings.Aspect.Annotation.ToValidate)")
  public Object validateIncomingAnonymousChatMessage(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    if (args[0] instanceof StompSendPayload payload && args[1] instanceof Principal principal) {
      UUID conversationId = payload.getConversationId();
      String userId = principal.getName();
      if (userId.startsWith("anon:")) {
        AnonymousConversation conversation =
            anonymousConversationDao
                .findById(conversationId)
                .orElseThrow(ConversationNotFoundException::new);
        if (conversation.getParticipants().contains(new ConversationMemberDetails(userId))) {
          return pjp.proceed();
        }
      } else {
        Conversation conversation =
            conversationDao
                .findById(conversationId)
                .orElseThrow(ConversationNotFoundException::new);
        User user = userService.findUserById(UUID.fromString(userId)).orElseThrow();
        if (conversation.getMembers().contains(user)) {
          return pjp.proceed();
        }
      }
      LOG.warn("User is not a part of the conversation.");
      throw new NonConversationMemberException("User is not a member of the conversation");
    }

    throw new RuntimeException("Incorrect parameter types.");
  }

  @Around("execution(* com.gabcytn.strangerstrings.Controller.MessageRestController.get(..))")
  public Object validate(ProceedingJoinPoint pjp) throws Throwable {
    if (pjp.getArgs()[0] instanceof UUID conversationId) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // TODO: cache results
      User user =
          userService
              .findUserByUsername(((UserPrincipal) authentication.getPrincipal()).getUsername())
              .orElseThrow(UserNotFoundException::new);
      if (redisQueueService.isMemberOfConversation(user.getId(), conversationId)) {
        return pjp.proceed();
      }
      LOG.warn("User is not a part of the conversation.");
      throw new NonConversationMemberException("User is not a member of the conversation");
    }

    return null;
  }
}
