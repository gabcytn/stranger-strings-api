package com.gabcytn.strangerstrings.Aspect;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InterestInterceptorAspect {
  private static final Logger LOG = LoggerFactory.getLogger(InterestInterceptorAspect.class);

  @Around("execution(* com.gabcytn.strangerstrings.Controller.MessagingController.queue(..))")
  public Object interestInterceptor(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    LOG.info("Args: {}", args);
    if (args[0] instanceof ChatInitiationDto reqBody && args[1] instanceof Principal principal) {
      if (principal.getName().startsWith("anon:")) {
        List<String> prefixedInterests = new ArrayList<>();
        reqBody
            .getInterests()
            .forEach(
                interest -> {
                  prefixedInterests.add("anon:" + interest);
                });
        ChatInitiationDto dto = new ChatInitiationDto();
        dto.setInterests(prefixedInterests);
        LOG.info("New request: {}", dto);
        return pjp.proceed(new Object[] {dto, principal});
      }
      LOG.info("Principal name: {}", principal.getName());
      return pjp.proceed();
    }

    throw new RuntimeException("Incorrect parameters.");
  }
}
