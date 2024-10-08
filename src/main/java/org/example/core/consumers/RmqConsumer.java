package org.example.core.consumers;

import static org.example.api.Constants.TRACE_ID_KEY;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class RmqConsumer extends DefaultConsumer {

  public RmqConsumer(Channel channel) {
    super(channel);
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
    String routingKey = envelope.getRoutingKey();
    long deliveryTag = envelope.getDeliveryTag();
    String msg = new String(body);

    try {
      MDC.put(TRACE_ID_KEY, UUID.randomUUID().toString());
      simulateIntensiveTask(msg);
      log.info("{} :: {} :: {} :: {}", msg, routingKey, consumerTag, deliveryTag);
      getChannel().basicAck(deliveryTag, false);
    } catch (Exception e) {
      log.error("Failed to consume msg due to ", e);
      getChannel().basicReject(deliveryTag, false);
    } finally {
      MDC.clear();
    }
  }

  private void simulateIntensiveTask(String msg) throws Exception {
    for (char ch : msg.toCharArray()) {
      if (ch == '.') {
        Thread.sleep(1000);
      } else if (ch == '*') {
        throw new IllegalArgumentException("* not allowed in msg");
      }
    }
  }
}
