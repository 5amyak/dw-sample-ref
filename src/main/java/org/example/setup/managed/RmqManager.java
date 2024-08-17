package org.example.setup.managed;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.dropwizard.lifecycle.Managed;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public static final String DLQ_SUFFIX = "_DLQ";
  public static final String RMQ_DLE = "dle";
  private Connection rmqConn;
  private final List<Channel> rmqChannelList = new ArrayList<>();
  private final RmqConfig rmqConfig;
  @Getter
  private RmqProducer rmqProducer;

  public static final String DEFAULT_DIRECT_EXCHANGE = "amq.direct";

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
    rmqProducer = new RmqProducer(rmqConn);

    // create consumers based on concurrency
    for (int i = 0; i < rmqConfig.getConcurrencyCount(); i++) {
      Channel rmqChannel = rmqConn.createChannel();
      rmqChannel.basicQos(rmqConfig.getPrefetchCount());

      rmqChannel.exchangeDeclare(RMQ_DLE, BuiltinExchangeType.DIRECT, true);
      rmqChannel.queueDeclare(rmqConfig.getQueueName() + DLQ_SUFFIX, true, false, true, null);
      rmqChannel.queueBind(rmqConfig.getQueueName() + DLQ_SUFFIX, RMQ_DLE, rmqConfig.getQueueName());

      Map<String, Object> args = new HashMap<>();
      args.put("x-dead-letter-exchange", RMQ_DLE);
      args.put("x-max-length", rmqConfig.getMaxLength());
      rmqChannel.queueDeclare(rmqConfig.getQueueName(), true, false, true, args);
      rmqChannel.queueBind(rmqConfig.getQueueName(), DEFAULT_DIRECT_EXCHANGE, rmqConfig.getQueueName());

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
