package org.example.setup.managed;

import io.dropwizard.lifecycle.Managed;
import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;

@Slf4j
public class KafkaManager implements Managed {

  public static final String TOPIC_NAME = "t1";
  @Getter
  private final Consumer<String, String> kafkaConsumer;
  @Getter
  private final Producer<String, String> kafkaProducer;
  private volatile boolean shoudConsumeMessages = true;
  private Thread consumerThread;

  public KafkaManager(Consumer<String, String> kafkaConsumer, Producer<String, String> kafkaProducer) {
    this.kafkaConsumer = kafkaConsumer;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public void start() {
    consumerThread = new Thread(() -> {
      shoudConsumeMessages = true;
      kafkaConsumer.subscribe(List.of(TOPIC_NAME));
      while (shoudConsumeMessages) {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
        records.forEach(record -> log.info("Consumed message: {}, from partition: {}", record.value(), record.partition()));
      }
    });

    consumerThread.start();
  }

  @Override
  public void stop() throws InterruptedException {
    shoudConsumeMessages = false;
    consumerThread.join();
    kafkaConsumer.close();
    kafkaProducer.close();
  }

  public void pauseConsumer() throws InterruptedException {
    shoudConsumeMessages = false;
    consumerThread.join();
    kafkaConsumer.unsubscribe();
  }

}
