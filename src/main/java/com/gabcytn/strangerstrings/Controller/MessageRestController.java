package com.gabcytn.strangerstrings.Controller;

import com.gabcytn.strangerstrings.DAO.MessageDao;
import com.gabcytn.strangerstrings.Model.PaginatedMessageContent;
import com.gabcytn.strangerstrings.DTO.PaginatedMessageResponse;
import com.gabcytn.strangerstrings.Entity.Message;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Model.ConversationMemberDetails;
import java.util.ArrayList;
import java.util.List;
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

  public MessageRestController(MessageDao messageDao) {
    this.messageDao = messageDao;
  }

  @GetMapping
  public PaginatedMessageResponse get(@RequestParam int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10);
    Page<Message> messages = messageDao.findAll(pageable);
    PaginatedMessageResponse response = new PaginatedMessageResponse();
    response.setPageable(pageable);

    List<PaginatedMessageContent> contents = new ArrayList<>();
    messages.forEach(
        message -> {
          PaginatedMessageContent content = new PaginatedMessageContent();
          content.setId(message.getId());
          content.setBody(message.getBody());
          content.setConversationId(message.getConversation().getId());

          User user = message.getSender();
          ConversationMemberDetails memberDetails =
              new ConversationMemberDetails(
                  "auth:" + user.getId().toString(), user.getUsername(), user.getProfilePic());
          content.setSender(memberDetails);
          content.setDate(message.getCreatedAt());
          contents.add(content);
        });

    response.setContent(contents);
    return response;
  }
}
