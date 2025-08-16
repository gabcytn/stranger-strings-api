package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Service.ConversationService;
import com.gabcytn.strangerstrings.Service.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessagingController {
  private final MessagingService messagingService;
  private final ConversationService conversationService;

  public MessagingController(
      MessagingService messagingService, ConversationService conversationService) {
    this.messagingService = messagingService;
    this.conversationService = conversationService;
  }

  @MessageMapping("/match")
  public void queue(@RequestBody @Valid ChatInitiationDto chatInitiationDto, Principal principal) {
    messagingService.queue(chatInitiationDto, principal.getName());
  }

  @MessageMapping("/chat.send")
  public void message(@RequestBody @Valid StompSendPayload payload, Principal principal) {
    Conversation conversation =
        conversationService
            .getConversation(payload.getConversationId())
            .orElseThrow(
                () -> new ConversationNotFoundException("The conversation field is invalid."));
    messagingService.message(payload.getMessage(), principal, conversation);
  }
}
