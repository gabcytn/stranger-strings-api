package com.gabcytn.strangerstrings.DTO;

public class WebSocketErrorResponse {
  private String title;
  private String error;
  private String description;

  public WebSocketErrorResponse() {}

  public WebSocketErrorResponse(String title, String error, String description) {
    this.title = title;
    this.error = error;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "WebSocketErrorResponse{"
        + "title='"
        + title
        + '\''
        + ", error='"
        + error
        + '\''
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
