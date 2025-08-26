package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.Aspect.Annotation.ToValidate;
import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.InterestMatchedResponse;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Exception.NoInterestMatchException;
import com.gabcytn.strangerstrings.Model.ConversationMemberDetails;
import com.gabcytn.strangerstrings.Model.MessageServiceQueueingResponse;
import com.gabcytn.strangerstrings.Service.ConversationService;
import com.gabcytn.strangerstrings.Service.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingController.class);
  private final MessagingService messagingService;
  private final ConversationService conversationService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public MessagingController(
      MessagingService messagingService,
      ConversationService conversationService,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.messagingService = messagingService;
    this.conversationService = conversationService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/match")
  public void queue(@RequestBody @Valid ChatInitiationDto chatInitiationDto, Principal principal) {
    String userId = principal.getName();
    MessageServiceQueueingResponse serviceResponse;
    if (userId.startsWith("anon:")) {
      serviceResponse =
          messagingService
              .queueOfAnonymous(chatInitiationDto.getInterests(), userId)
              .orElseThrow(() -> new NoInterestMatchException(chatInitiationDto.getInterests()));
    } else {
      serviceResponse =
          messagingService
              .queueOfAuthenticated(chatInitiationDto.getInterests(), userId)
              .orElseThrow(() -> new NoInterestMatchException(chatInitiationDto.getInterests()));
    }
    LOG.info("Service response: {}", serviceResponse);
    InterestMatchedResponse matchResponse = this.getPayloadToSend(serviceResponse);
    this.sendToMatchedUsers(
        serviceResponse.getMembers().stream().map(ConversationMemberDetails::getUserId).toList(),
        matchResponse);
    LOG.info(
        "Successfully matched {} users in {} interests.",
        serviceResponse.getMembers().size(),
        serviceResponse.getInterest());
  }

  @MessageMapping("/chat.send")
  @ToValidate
  public void message(@RequestBody @Valid StompSendPayload payload, Principal principal) {
    Conversation conversation =
        conversationService
            .getConversation(payload.getConversationId())
            .orElseThrow(
                () -> new ConversationNotFoundException("The conversation field is invalid."));
    messagingService.message(payload.getMessage(), principal, conversation);
  }

  private InterestMatchedResponse getPayloadToSend(MessageServiceQueueingResponse response) {
    return new InterestMatchedResponse(
        response.getInterest(), response.getConversationId(), response.getMembers());
  }

  private void sendToMatchedUsers(List<String> users, Object payload) {
    LOG.info("Users matched: {}", users);
    users.forEach(
        user -> {
          simpMessagingTemplate.convertAndSendToUser(user, "/topic/match", payload);
        });
  }

  // TODO: typing indicator
}
