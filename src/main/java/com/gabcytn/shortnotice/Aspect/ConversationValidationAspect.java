package com.gabcytn.shortnotice.Aspect;

import com.gabcytn.shortnotice.DTO.StompSendPayload;
import com.gabcytn.shortnotice.Service.RedisQueueService;
import java.security.Principal;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConversationValidationAspect {
  private static final Logger LOG = LoggerFactory.getLogger(ConversationValidationAspect.class);
  private final RedisQueueService redisQueueService;

  public ConversationValidationAspect(RedisQueueService redisQueueService) {
    this.redisQueueService = redisQueueService;
  }

  @Around(
      "execution(* com.gabcytn.shortnotice.Controller.AnonymousMessagingController.message(..))")
  public Object validateIncomingAnonymousChatMessage(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    if (args[0] instanceof StompSendPayload payload && args[1] instanceof Principal principal) {
      UUID conversationId = payload.getConversationId();
      UUID userId = UUID.fromString(principal.getName());
      if (redisQueueService.isMemberOfConversation(userId, conversationId)) {
        return pjp.proceed();
      }
      LOG.warn("User is not a part of the conversation.");
      return null;
    }

    LOG.info("Incorrect parameter types.");
    return null;
  }
}
