package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@MessageMapping("/anonymous")
public class AnonymousMessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingController.class);
  private final MessagingService messagingService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public AnonymousMessagingController(
      MessagingService messagingService, SimpMessagingTemplate simpMessagingTemplate) {
    this.messagingService = messagingService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/matcher")
  public void match(@RequestBody @Valid ChatInitiationDto reqBody, Principal principal) {
    try {
      QueueMatchedResponse<? extends ConversationMember> response =
          messagingService
              .match(reqBody.getInterests(), UUID.fromString(principal.getName()))
              .orElseThrow(RuntimeException::new);
      Set<UUID> memberIds =
          response.getMembers().stream().map(ConversationMember::getId).collect(Collectors.toSet());
      this.sendConversationDetailsToMatchedUsers(memberIds, response);
    } catch (RuntimeException e) {
      LOG.info("Waiting a match for: {}", reqBody.getInterests());
    }
  }

  private void sendConversationDetailsToMatchedUsers(Set<UUID> members, Object payload) {
    members.forEach(
        member -> {
          simpMessagingTemplate.convertAndSendToUser(member.toString(), "/topic/anonymous/match", payload);
        });
  }
}
