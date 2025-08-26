package com.gabcytn.strangerstrings;

import com.gabcytn.strangerstrings.DTO.ChatInitiationDto;
import com.gabcytn.strangerstrings.DTO.InterestMatchedResponse;
import com.gabcytn.strangerstrings.Helper.MyStompSessionHandler;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketTest.class);
  private WebSocketStompClient stompClient;
  private WebSocketStompClient stompClient2;
  @LocalServerPort private Integer port;

  public WebSocketTest(
      @Value("${spring.data.redis.users-interests-map}") String key,
      @Qualifier("redisUsersInterestsMapTemplate")
          RedisTemplate<String, Map<String, List<String>>> redisTemplate) {
    Map<String, List<String>> map = new HashMap<>();
    map.put("_init", List.of());
    redisTemplate.opsForValue().set(key, map);
  }

  @BeforeEach
  public void init() {
    this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    this.stompClient2 = new WebSocketStompClient(new StandardWebSocketClient());
  }

  @Test
  void testQueueMatching() throws InterruptedException {
    List<MessageConverter> messageConverters = new ArrayList<>();
    messageConverters.add(new StringMessageConverter());
    messageConverters.add(new MappingJackson2MessageConverter());
    stompClient.setMessageConverter(new CompositeMessageConverter(messageConverters));
    stompClient2.setMessageConverter(new CompositeMessageConverter(messageConverters));

    CountDownLatch connectionLatch = new CountDownLatch(2); // Track connections
    CountDownLatch subscriptionLatch = new CountDownLatch(2); // Track subscriptions
    CountDownLatch messageLatch = new CountDownLatch(2); // Track received messages

    AtomicReference<UUID> clientOneConversationId = new AtomicReference<>();
    AtomicReference<UUID> clientTwoConversationId = new AtomicReference<>();

    String destination = "/user/topic/match";

    // Client 1 setup
    stompClient.connectAsync(
        getWsPath(),
        new StompSessionHandlerAdapter() {
          @Override
          public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            LOG.info("Client 1 connected");
            connectionLatch.countDown();

            session.subscribe(
                destination,
                new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                    return InterestMatchedResponse.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                    LOG.info("Client 1 received match confirmation: {}", payload);
                    if (payload instanceof InterestMatchedResponse s) {
                      messageLatch.countDown();
                      clientOneConversationId.set(s.getConversationId());
                    }
                  }
                });
            LOG.info("Client 1 subscribed to {}", destination);
            subscriptionLatch.countDown();

            ChatInitiationDto dto = new ChatInitiationDto();
            dto.setInterests(List.of("java", "spring"));
            session.send("/app/match", dto);
          }

          @Override
          public void handleException(
              StompSession session,
              StompCommand command,
              StompHeaders headers,
              byte[] payload,
              Throwable exception) {
            LOG.error("Client 1 error: {}", exception.getMessage());
          }
        });

    // FIX: concurrency issues
    Thread.sleep(500);

    // Client 2 setup
    stompClient2.connectAsync(
        getWsPath(),
        new StompSessionHandlerAdapter() {
          @Override
          public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            LOG.info("Client 2 connected");
            connectionLatch.countDown();

            session.subscribe(
                destination,
                new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                    return InterestMatchedResponse.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                    LOG.info("Client 2 received match confirmation: {}", payload);
                    if (payload instanceof InterestMatchedResponse s) {
                      messageLatch.countDown();
                      clientTwoConversationId.set(s.getConversationId());
                    }
                  }
                });

            LOG.info("Client 2 subscribed to {}", destination);
            subscriptionLatch.countDown();

            ChatInitiationDto dto = new ChatInitiationDto();
            dto.setInterests(List.of("java", "spring"));
            session.send("/app/match", dto);
          }

          @Override
          public void handleException(
              StompSession session,
              StompCommand command,
              StompHeaders headers,
              byte[] payload,
              Throwable exception) {
            LOG.error("Client 2 error: {}", exception.getMessage());
          }
        });

    // Wait for both clients to connect and subscribe
    Assertions.assertTrue(
        connectionLatch.await(5, TimeUnit.SECONDS), "Clients should connect within 5 seconds");
    Assertions.assertTrue(
        subscriptionLatch.await(5, TimeUnit.SECONDS), "Clients should subscribe within 5 seconds");

    // Wait for match confirmations
    boolean matchResult = messageLatch.await(5, TimeUnit.SECONDS);
    Assertions.assertTrue(
        matchResult, "Both clients should receive match confirmation within 10 seconds");

    Assertions.assertEquals(clientOneConversationId.get(), clientTwoConversationId.get());
    LOG.info("Client 1 response: {}", clientOneConversationId.get());
    LOG.info("Client 2 response: {}", clientTwoConversationId.get());

    LOG.info("Queue matching test completed successfully");
  }

  private String getWsPath() {
    return String.format("ws://localhost:%d/ws/v1/stranger-strings", port);
  }
}
