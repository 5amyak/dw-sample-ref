package org.example.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.clients.producer.Producer;
import org.example.setup.managed.RmqManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AsyncMsgResourceUT {

  private final RmqManager rmqManager = mock(RmqManager.class, RETURNS_MOCKS);
  private final Producer kafkaProducer = mock(Producer.class, RETURNS_MOCKS);
  private final ResourceExtension resourceExtension = ResourceExtension.builder()
      .addResource(new AsyncMsgResource(rmqManager, kafkaProducer))
      .build();

  @Test
  public void testPublishMsgToRMQ() {
    // Arrange
    String msg = "Hello, World!";
    String rk = "test-routing-key";

    // Act
    Response response = resourceExtension.target("/async/rmq")
        .queryParam("routingKey", rk)
        .request()
        .post(Entity.text(msg));

    // Assert
    assertEquals(200, response.getStatus());
    assertEquals(msg, response.readEntity(String.class));
  }

  @Test
  public void testPublishMsgToKafka() {
    // Arrange
    String msg = "Hello, World!";
    String topic = "test-topic";

    // Act
    Response response = resourceExtension.target("/async/kafka")
        .queryParam("topic", topic)
        .request()
        .post(Entity.text(msg));

    // Assert
    assertEquals(200, response.getStatus());
    assertEquals(msg, response.readEntity(String.class));
  }
}