package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.StompSendPayload;
import com.gabcytn.strangerstrings.DTO.WebSocketErrorResponse;
import com.gabcytn.strangerstrings.Service.MessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingController.class);
  private final MessagingService messagingService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public MessagingController(
      MessagingService messagingService, SimpMessagingTemplate simpMessagingTemplate) {
    this.messagingService = messagingService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/match")
  void queue(@RequestBody @Valid ChatInitiationDto chatInitiationDto, Principal principal) {
    messagingService.queue(chatInitiationDto, principal.getName());
  }

  @MessageMapping("/chat.send")
  public void message(@RequestBody @Valid StompSendPayload payload, Principal principal) {
    messagingService.message(payload, UUID.fromString(principal.getName()));
  }

  // error handler for input validation in websockets
  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  void handleValidationException(MethodArgumentNotValidException exception, Principal principal) {
    LOG.error("MethodArgumentNotValidException raised.");
    assert exception.getBindingResult() != null;
    String errorMessage =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

    // send response
    WebSocketErrorResponse response = new WebSocketErrorResponse();
    response.setTitle("Invalid payload.");
    response.setError(MethodArgumentNotValidException.class.toString());
    response.setDescription(errorMessage);
    simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
  }
}
