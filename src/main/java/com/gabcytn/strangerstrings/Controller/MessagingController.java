package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.Aspect.Annotation.ToValidate;
import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.InterestMatchedResponse;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Exception.NoInterestMatchException;
import com.gabcytn.strangerstrings.Model.ConversationMemberDetails;
import com.gabcytn.strangerstrings.Model.MessageServiceQueueingResponse;
import com.gabcytn.strangerstrings.Service.ConversationService;
import com.gabcytn.strangerstrings.Service.MessagingService;
import com.gabcytn.strangerstrings.Service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
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
public class MessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingController.class);
  private final MessagingService messagingService;
  private final ConversationService conversationService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final UserService userService;

  public MessagingController(
      MessagingService messagingService,
      ConversationService conversationService,
      SimpMessagingTemplate simpMessagingTemplate,
      UserService userService) {
    this.messagingService = messagingService;
    this.conversationService = conversationService;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.userService = userService;
  }

  @MessageMapping("/match")
  public void queue(@RequestBody @Valid ChatInitiationDto chatInitiationDto, Principal principal) {
    try {
      MessageServiceQueueingResponse serviceResponse =
          messagingService
              .queue(chatInitiationDto, principal.getName())
              .orElseThrow(NoInterestMatchException::new);
      this.sendConversationDetailsToMatchedUsers(serviceResponse);
      LOG.info(
          "Successfully matched {} users in {} interests.",
          serviceResponse.getMembers().size(),
          serviceResponse.getInterest());
    } catch (NoInterestMatchException e) {
      LOG.info("Waiting for other users to be matched...");
    }
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

  private void sendConversationDetailsToMatchedUsers(
      MessageServiceQueueingResponse serviceResponse) {
    Set<String> members =
        serviceResponse.getMembers().stream().map(Object::toString).collect(Collectors.toSet());
    Set<ConversationMemberDetails> participants =
        this.getConversationMemberDetails(serviceResponse, members);

    InterestMatchedResponse response =
        new InterestMatchedResponse(
            this.getInterestWithoutPrefix(serviceResponse.getInterest()),
            serviceResponse.getConversation().getId(),
            participants);

    for (Object sessionId : serviceResponse.getMembers()) {
      messagingService.removeFromInterestsSet((String) sessionId);
      simpMessagingTemplate.convertAndSendToUser((String) sessionId, "/topic/match", response);
    }
  }

  private Set<ConversationMemberDetails> getConversationMemberDetails(
      MessageServiceQueueingResponse serviceResponse, Set<String> members) {
    Set<ConversationMemberDetails> participants = new HashSet<>();
    if (this.isAuthenticated(serviceResponse.getInterest())) {
      members.forEach(
          member -> {
            User user = userService.findUserById(UUID.fromString(member)).orElseThrow();
            ConversationMemberDetails details =
                new ConversationMemberDetails(member, user.getUsername(), user.getProfilePic());
            participants.add(details);
          });
    } else {
      members.forEach(
          member -> {
            participants.add(new ConversationMemberDetails(member, null, null));
          });
    }

    return participants;
  }

  private Boolean isAuthenticated(String interest) {
    return this.getInterestWithoutPrefix(interest).equals("auth:");
  }

  private String getInterestWithoutPrefix(String interest) {
    return interest.substring(5);
  }

  // TODO: typing indicator
}
