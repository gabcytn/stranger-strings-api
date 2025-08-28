package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.Aspect.Annotation.NoDuplicateRequest;
import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.Model.ConversationMember;
import com.gabcytn.strangerstrings.Model.QueueMatchedResponse;
import com.gabcytn.strangerstrings.Service.Interface.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebSocketMessagingController {
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

  @NoDuplicateRequest
  @MessageMapping("/authenticated/matcher")
  public void authenticatedMatcher(
      @RequestBody @Valid ChatInitiationDto reqBody, Principal principal) {
    this.match(
        authMessagingService,
        reqBody.getInterests(),
        UUID.fromString(principal.getName()),
        "authenticated");
  }

  @NoDuplicateRequest
  @MessageMapping("/anonymous/matcher")
  public void anonymousMatcher(@RequestBody @Valid ChatInitiationDto reqBody, Principal principal) {
    this.match(
        anonMessagingService,
        reqBody.getInterests(),
        UUID.fromString(principal.getName()),
        "anonymous");
  }

  private void match(
      MessagingService messagingService, List<String> interests, UUID userId, String destination) {
    Optional<QueueMatchedResponse<? extends ConversationMember>> response =
        messagingService.match(interests, userId);

    if (response.isEmpty()) return;

    this.sendConversationDetailsToMatchedUsers(response.get().getMembers(), destination, response);
  }

  private void sendConversationDetailsToMatchedUsers(
      Set<? extends ConversationMember> members, String destination, Object payload) {
    members.forEach(
        member -> {
          simpMessagingTemplate.convertAndSendToUser(
              member.getId().toString(), String.format("/topic/%s/matcher", destination), payload);
        });
  }

  @MessageMapping
  public void message(StompSendPayload reqBody, Principal principal) {}
}
