package org.example.setup.managed;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.dropwizard.lifecycle.Managed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.core.consumers.RmqConsumer;
import org.example.core.producers.RmqProducer;
import org.example.setup.configs.QueueConfig;
import org.example.setup.configs.RmqConfig;

/*
  Protocol: https://www.rabbitmq.com/tutorials/amqp-concepts
  Tutorial: https://www.rabbitmq.com/tutorials/tutorial-one-java
  API Docs: https://www.rabbitmq.com/client-libraries/java-api-guide
*/
@Slf4j
public class RmqManager implements Managed {

  private Connection rmqConn;
  private final List<Channel> rmqConsumerChannels = new ArrayList<>();
  private final RmqConfig rmqConfig;
  @Getter
  private RmqProducer rmqProducer;

  public static final String DEFAULT_DIRECT_EXCHANGE = "amq.direct";
  public static final String DLQ_SUFFIX = "_DLQ";
  public static final String RMQ_DLE = "dle";

  public RmqManager(RmqConfig rmqConfig) {
    this.rmqConfig = rmqConfig;
  }

  @Override
  public void start() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(rmqConfig.getUri());
    factory.setVirtualHost("/");

    // create connection
    rmqConn = factory.newConnection(rmqConfig.getConnName());

    // create producer
    rmqProducer = new RmqProducer(rmqConn);

    // create consumers based on concurrency
    this.startConsumers();
    Managed.super.start();
  }

  public void startConsumers() throws IOException {
    if (!rmqConsumerChannels.isEmpty()) {
      return;
    }

    for (QueueConfig queueConfig : rmqConfig.getQueues()) {
      for (int i = 0; i < queueConfig.getConcurrencyCount(); i++) {
        Channel rmqChannel = rmqConsumerInit(queueConfig, i);
        rmqConsumerChannels.add(rmqChannel);
        log.info("Successfully created consumer for queue :: {}", queueConfig.getName());
      }
    }
  }

  private Channel rmqConsumerInit(QueueConfig queueConfig, int i) throws IOException {
    Channel rmqChannel = rmqConn.createChannel();
    rmqChannel.basicQos(queueConfig.getPrefetchCount());
    rmqChannel.exchangeDeclare(RMQ_DLE, BuiltinExchangeType.DIRECT, true);

    rmqChannel.queueDeclare(queueConfig.getName() + DLQ_SUFFIX, true, false, true, null);
    rmqChannel.queueBind(queueConfig.getName() + DLQ_SUFFIX, RMQ_DLE, queueConfig.getName());

    Map<String, Object> args = new HashMap<>();
    args.put("x-dead-letter-exchange", RMQ_DLE);
    args.put("x-max-length", queueConfig.getMaxLength());
    rmqChannel.queueDeclare(queueConfig.getName(), true, false, true, args);
    rmqChannel.queueBind(queueConfig.getName(), DEFAULT_DIRECT_EXCHANGE, queueConfig.getName());

    rmqChannel.basicConsume(queueConfig.getName(), false,
        queueConfig.getPrefix() + "consumer" + i, new RmqConsumer(rmqChannel));
    return rmqChannel;
  }

  @Override
  public void stop() throws Exception {
    this.stopConsumers();
    rmqProducer.close();
    if (rmqConn != null) {
      rmqConn.close();
      log.info("RMQ connection :: {} is closed", rmqConn.getClientProvidedName());
    }
    Managed.super.stop();
  }

  public void stopConsumers() throws IOException, TimeoutException {
    Iterator<Channel> it = rmqConsumerChannels.iterator();
    while (it.hasNext()) {
      Channel rmqChannel = it.next();
      rmqChannel.close();
      log.info("RMQ channel :: {} is closed", rmqChannel.getChannelNumber());
      it.remove();
    }
  }
}
