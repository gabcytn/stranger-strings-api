package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.DTO.PaginatedMessageResponse;
import com.gabcytn.strangerstrings.Entity.Conversation;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.ConversationNotFoundException;
import com.gabcytn.strangerstrings.Model.AuthenticatedConversationMember;
import com.gabcytn.strangerstrings.Model.PaginatedMessageContent;
import com.gabcytn.strangerstrings.Service.ConversationService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
  private final MessageDao messageDao;
  private final ConversationService conversationService;

  public MessageRestController(MessageDao messageDao, ConversationService conversationService) {
    this.messageDao = messageDao;
    this.conversationService = conversationService;
  }

  // FIX: minimize function size (i.e. split up)
  // TODO: handle 'not found' exceptions
  @GetMapping
  public PaginatedMessageResponse get(
      @RequestParam UUID conversationId, @RequestParam int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10);
    // TODO: use a caching service together with
    // com.gabcytn.strangerstrings.Service.UserDetailsService
    Conversation conversation =
        conversationService
            .getConversation(conversationId)
            .orElseThrow(ConversationNotFoundException::new);
    Page<Message> messages = messageDao.findByConversation(conversation, pageable);
    PaginatedMessageResponse response = new PaginatedMessageResponse();
    response.setPageable(pageable);

    List<PaginatedMessageContent> contents = new ArrayList<>();
    messages.forEach(
        message -> {
          PaginatedMessageContent content = new PaginatedMessageContent();
          content.setId(message.getId());
          content.setBody(message.getBody());
          content.setConversationId(message.getConversation().getId());

          User sender = message.getSender();
          content.setSender(sender.getId());
          content.setDate(message.getCreatedAt());
          contents.add(content);
        });

    response.setContent(contents);
    return response;
  }
}
