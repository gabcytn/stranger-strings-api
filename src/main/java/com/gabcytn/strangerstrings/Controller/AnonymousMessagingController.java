package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.InterestMatchedResponse;
import com.gabcytn.strangerstrings.Model.MessageServiceQueueingResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/anonymous")
public class AnonymousMessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingController.class);
  private final MessagingService messagingService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public AnonymousMessagingController(
      MessagingService messagingService, SimpMessagingTemplate simpMessagingTemplate) {
    this.messagingService = messagingService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/match")
  public void match(@RequestBody @Valid ChatInitiationDto reqBody, Principal principal) {
    try {
      MessageServiceQueueingResponse response =
          messagingService
              .match(reqBody.getInterests(), UUID.fromString(principal.getName()))
              .orElseThrow(RuntimeException::new);
      InterestMatchedResponse matchedResponse =
          new InterestMatchedResponse(
              response.getInterest(), response.getConversationId(), response.getMembers());
      this.sendConversationDetailsToMatchedUsers(Set.of(), matchedResponse);
    } catch (RuntimeException e) {
      LOG.info("Waiting a match for: {}", reqBody.getInterests());
    }
  }

  private void sendConversationDetailsToMatchedUsers(Set<UUID> members, Object payload) {
    members.forEach(
        member -> {
          simpMessagingTemplate.convertAndSendToUser(member.toString(), "/topic/match", payload);
        });
  }
}
