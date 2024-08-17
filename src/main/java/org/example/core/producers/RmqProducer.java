package org.example.core.producers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import lombok.Getter;

@Getter
public class RmqProducer implements AutoCloseable {

  private final String routingKey;
  private final Channel rmqChannel;
  private final ExecutorService executorService;

  public RmqProducer(String routingKey, Connection rmqConn) throws IOException {
    this.routingKey = routingKey;
    this.rmqChannel = rmqConn.createChannel();
    this.executorService = Executors.newSingleThreadExecutor();
  }

  public void publish(byte[] msg) {
    executorService.submit(() -> {
      try {
        rmqChannel.basicPublish("", routingKey,
            MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void close() throws IOException, TimeoutException {
    executorService.shutdown();
    rmqChannel.close();
  }

}
