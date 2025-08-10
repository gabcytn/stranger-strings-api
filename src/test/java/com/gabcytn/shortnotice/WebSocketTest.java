package com.gabcytn.shortnotice;

import com.gabcytn.shortnotice.Helper.MyStompSessionHandler;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {
  @LocalServerPort private Integer port;

  @Test
  void testReceivedMessageOnSubscribe() throws InterruptedException, ExecutionException {
    WebSocketClient webSocketClient = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
    stompClient.setMessageConverter(new StringMessageConverter());

    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> receivedMessage = new AtomicReference<>();
    String destination = "/user/topic/anonymous/queue";
    StompSessionHandler stompSessionHandler =
        new MyStompSessionHandler(destination, latch, receivedMessage);
    stompClient.connectAsync(getWsPath(), stompSessionHandler).get();

    boolean isMessageReceived = latch.await(1, TimeUnit.SECONDS);
    Assertions.assertTrue(isMessageReceived);
    Assertions.assertEquals(
        receivedMessage.get(), String.format("Successfully subscribed to: %s", destination));
  }

  private String getWsPath() {
    return String.format("ws://localhost:%d/short-notice", port);
  }
}
