package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebSocketMessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketMessagingController.class);
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final MessagingService anonMessagingService;
  private final MessagingService authMessagingService;

  protected WebSocketMessagingController(
      @Qualifier("AnonMessagingService") MessagingService anonMessagingService,
      @Qualifier("AuthMessagingService") MessagingService authMessagingService,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.anonMessagingService = anonMessagingService;
    this.authMessagingService = authMessagingService;
  }

  @MessageMapping("/authenticated/matcher")
  public void authenticatedMatcher(ChatInitiationDto reqBody, Principal principal) {
    this.match(
        authMessagingService,
        reqBody.getInterests(),
        UUID.fromString(principal.getName()),
        "authenticated");
  }

  @MessageMapping("/anonymous/matcher")
  public void anonymousMatcher(@RequestBody @Valid ChatInitiationDto reqBody, Principal principal) {
    this.match(
        anonMessagingService,
        reqBody.getInterests(),
        UUID.fromString(principal.getName()),
        "anonymous");
  }

  // TODO: use aspect to check if user is already in a queue.
  private void match(
      MessagingService messagingService, List<String> interests, UUID userId, String destination) {
    try {
      QueueMatchedResponse<? extends ConversationMember> response =
          messagingService.match(interests, userId).orElseThrow(RuntimeException::new);
      this.sendConversationDetailsToMatchedUsers(response.getMembers(), destination, response);
    } catch (RuntimeException e) {
      LOG.info("Waiting a match for: {}", interests);
    }
  }

  private void sendConversationDetailsToMatchedUsers(
      Set<? extends ConversationMember> members, String destination, Object payload) {
    members.forEach(
        member -> {
          simpMessagingTemplate.convertAndSendToUser(
              member.getId().toString(), String.format("/topic/%s/matcher", destination) , payload);
        });
  }

  @MessageMapping
  public void message(StompSendPayload reqBody, Principal principal) {}
}
