package com.gabcytn.strangerstrings.DTO;

import com.gabcytn.strangerstrings.Model.PaginatedMessageContent;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginatedMessageResponse {
  private List<PaginatedMessageContent> content;
  private Pageable pageable;

  public List<PaginatedMessageContent> getContent() {
    return content;
  }

  public void setContent(List<PaginatedMessageContent> content) {
    this.content = content;
  }

  public Pageable getPageable() {
    return pageable;
  }

  public void setPageable(Pageable pageable) {
    this.pageable = pageable;
  }
}
