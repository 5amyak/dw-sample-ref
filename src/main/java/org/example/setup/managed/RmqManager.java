package org.example.setup.managed;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.dropwizard.lifecycle.Managed;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.core.consumers.RmqConsumer;
import org.example.core.producers.RmqProducer;
import org.example.setup.configs.RmqConfig;

/*
  Protocol: https://www.rabbitmq.com/tutorials/amqp-concepts
  Tutorial: https://www.rabbitmq.com/tutorials/tutorial-one-java
  API Docs: https://www.rabbitmq.com/client-libraries/java-api-guide
*/
@Slf4j
public class RmqManager implements Managed {

  private Connection rmqConn;
  private final List<Channel> rmqChannelList = new ArrayList<>();
  private final RmqConfig rmqConfig;
  @Getter
  private RmqProducer rmqProducer;

  public RmqManager(RmqConfig rmqConfig) {
    this.rmqConfig = rmqConfig;
  }

  private void init() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(rmqConfig.getUri());
    factory.setVirtualHost("/");

    // create connection
    rmqConn = factory.newConnection(rmqConfig.getPrefix() + "conn");

    // create producer
    rmqProducer = new RmqProducer(rmqConfig.getQueueName(), rmqConn);

    // create consumers based on concurrency
    for (int i = 0; i < rmqConfig.getConcurrencyCount(); i++) {
      Channel rmqChannel = rmqConn.createChannel();
      rmqChannel.basicQos(rmqConfig.getPrefetchCount());

      rmqChannel.queueDeclare(rmqConfig.getQueueName(), true, false, true, null);
      rmqChannel.basicConsume(rmqConfig.getQueueName(), false,
          rmqConfig.getPrefix() + "consumer" + i, new RmqConsumer(rmqChannel));
      rmqChannelList.add(rmqChannel);
    }
  }

  @Override
  public void start() throws Exception {
    init();
    Managed.super.start();
  }

  @Override
  public void stop() throws Exception {
    if (rmqConn != null) {
      rmqConn.close();
    }
    for (Channel rmqChannel : rmqChannelList) {
      rmqChannel.close();
    }
    Managed.super.stop();
  }
}
