package com.gabcytn.strangerstrings.Exception.Handler;

import com.gabcytn.strangerstrings.DTO.WebSocketErrorResponse;
import java.security.Principal;
import java.util.stream.Collectors;

import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Exception.NonConversationMemberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class WebSocketExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketExceptionHandler.class);
  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketExceptionHandler(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  public void handleInvalidRequestBody(
      MethodArgumentNotValidException exception, Principal principal) {
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
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
  }

  @MessageExceptionHandler(ConversationNotFoundException.class)
  public void handleConversationNotFound(
          ConversationNotFoundException exception, Principal principal) {
    LOG.error("{} raised.", ConversationNotFoundException.class.getName());
    WebSocketErrorResponse response =
            new WebSocketErrorResponse(
                    "Conversation not found.",
                    MethodArgumentNotValidException.class.getName(),
                    exception.getMessage());
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", response);
  }

  @MessageExceptionHandler(NonConversationMemberException.class)
  public void handleNonMemberConversation(NonConversationMemberException exception, Principal principal) {
    WebSocketErrorResponse errorResponse =
        new WebSocketErrorResponse(
            "Forbidden.",
                NonConversationMemberException.class.getName(),
            exception.getMessage());
    messagingTemplate.convertAndSendToUser(
        principal.getName(), "/queue/errors", errorResponse);
  }
}
