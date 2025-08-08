package com.gabcytn.shortnotice.Controller;

import com.gabcytn.shortnotice.DTO.ChatInitiationDto;
import com.gabcytn.shortnotice.DTO.WebSocketErrorResponse;
import com.gabcytn.shortnotice.Service.AnonymousMessagingService;
import jakarta.validation.Valid;
import java.security.Principal;
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
@MessageMapping("/anonymous")
public class AnonymousMessagingController {
  private static final Logger LOG = LoggerFactory.getLogger(AnonymousMessagingController.class);
  private final AnonymousMessagingService anonymousMessagingService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public AnonymousMessagingController(
      AnonymousMessagingService anonymousMessagingService,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.anonymousMessagingService = anonymousMessagingService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/queue")
  void queue(@RequestBody @Valid ChatInitiationDto chatInitiationDto, Principal principal) {
    anonymousMessagingService.queue(chatInitiationDto, principal.getName());
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
    simpMessagingTemplate.convertAndSendToUser(
        principal.getName(), "/topic/errors", response);
  }
}
