package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.DTO.PaginatedMessageResponse;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Model.PaginatedMessageContent;
import com.gabcytn.strangerstrings.Service.ConversationService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageRestController {
  private static final Logger LOG = LoggerFactory.getLogger(MessageRestController.class);
  private final MessageDao messageDao;
  private final ConversationService conversationService;

  public MessageRestController(MessageDao messageDao, ConversationService conversationService) {
    this.messageDao = messageDao;
    this.conversationService = conversationService;
  }

  // TODO: handle 'not found' exceptions
  @GetMapping
  public PaginatedMessageResponse get(
      @RequestParam UUID conversationId, @RequestParam int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10);
    Conversation conversation = this.getConversationById(conversationId);
    Page<Message> messages = messageDao.findByConversation(conversation, pageable);

    List<PaginatedMessageContent> contents = new ArrayList<>();
    for (Message message : messages) {
      contents.add(this.createPaginatedMessageContent(message));
    }

    return new PaginatedMessageResponse(contents, pageable);
  }

  private Conversation getConversationById(UUID conversationId) {
    try {
      return conversationService
          .getConversation(conversationId)
          .orElseThrow(ConversationNotFoundException::new);
    } catch (ConversationNotFoundException e) {
      LOG.error("Conversation id not found: {}", conversationId);
      throw new ConversationNotFoundException();
    }
  }

  private PaginatedMessageContent createPaginatedMessageContent(Message message) {
    return new PaginatedMessageContent(
        message.getId(),
        message.getBody(),
        message.getSender().getId(),
        message.getConversation().getId(),
        message.getCreatedAt());
  }
}
