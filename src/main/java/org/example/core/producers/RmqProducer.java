package org.example.core.producers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RmqProducer implements AutoCloseable {

  private final String queueName;
  private final Channel rmqChannel;
  private final ExecutorService executorService;

  public void publish(byte[] msg) {
    executorService.submit(() -> {
      try {
        rmqChannel.basicPublish("", queueName,
            MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void close() {
    executorService.shutdown();
  }

}
