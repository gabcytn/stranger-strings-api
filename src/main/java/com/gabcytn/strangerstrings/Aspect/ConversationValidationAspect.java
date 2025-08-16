package com.gabcytn.strangerstrings.Aspect;

import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.DTO.WebSocketErrorResponse;
import com.gabcytn.strangerstrings.Exception.NonConversationMemberException;
import com.gabcytn.strangerstrings.Service.RedisQueueService;
import java.security.Principal;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConversationValidationAspect {
  private static final Logger LOG = LoggerFactory.getLogger(ConversationValidationAspect.class);
  private final RedisQueueService redisQueueService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public ConversationValidationAspect(
      RedisQueueService redisQueueService, SimpMessagingTemplate simpMessagingTemplate) {
    this.redisQueueService = redisQueueService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @Around("execution(* com.gabcytn.strangerstrings.Controller.MessagingController.message(..))")
  public Object validateIncomingAnonymousChatMessage(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    if (args[0] instanceof StompSendPayload payload && args[1] instanceof Principal principal) {
      UUID conversationId = payload.getConversationId();
      UUID userId = UUID.fromString(principal.getName());
      if (redisQueueService.isMemberOfConversation(userId, conversationId)) {
        return pjp.proceed();
      }
      LOG.warn("User is not a part of the conversation.");
      WebSocketErrorResponse errorResponse =
          new WebSocketErrorResponse(
              "Forbidden.",
                  NonConversationMemberException.class.getName(),
              "You are not a part of the conversation.");
      simpMessagingTemplate.convertAndSendToUser(
          principal.getName(), "/queue/errors", errorResponse);
      return null;
    }

    LOG.info("Incorrect parameter types.");
    return null;
  }
}
