package com.gabcytn.strangerstrings.Helper;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(MyStompSessionHandler.class);

  private final String destination;
  private final CountDownLatch latch;
  private final AtomicReference<String> messageReceived;

  public MyStompSessionHandler(
      String destination, CountDownLatch latch, AtomicReference<String> messageReceived) {
    this.destination = destination;
    this.latch = latch;
    this.messageReceived = messageReceived;
  }

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return String.class;
  }

  @Override
  public void handleFrame(StompHeaders stompHeaders, Object o) {
    LOG.info("Handling frame...");
  }

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    session.subscribe(
        destination,
        new StompFrameHandler() {
          @Override
          public Type getPayloadType(StompHeaders headers) {
            return String.class;
          }

          @Override
          public void handleFrame(StompHeaders headers, Object payload) {
            LOG.info("Payload: {}", payload);
            messageReceived.set((String) payload);
            latch.countDown();
          }
        });
  }
}
