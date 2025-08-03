package com.gabcytn.shortnotice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  public void registerWithoutRequestBody() throws Exception {
    mockMvc
        .perform(post("/auth/register"))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void registerWithIncompleteRequestBody() throws Exception {
    String reqBody = getRegisterBody(false);
    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(reqBody))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void registerWithCompleteRequestBody() throws Exception {
    String reqBody = getRegisterBody(true);
    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(reqBody))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  private String getRegisterBody(boolean completeBody) throws Exception {
    Map<String, String> dto = new HashMap<>();
    dto.put("email", "randomemail@gmail.com");
    dto.put("password", "password");
    dto.put("username", "username");
    if (completeBody) dto.put("confirmPassword", "password");
    return objectMapper.writeValueAsString(dto);
  }
}
