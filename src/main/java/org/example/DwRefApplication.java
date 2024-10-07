package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.kafka.KafkaConsumerBundle;
import io.dropwizard.kafka.KafkaConsumerFactory;
import io.dropwizard.kafka.KafkaProducerBundle;
import io.dropwizard.kafka.KafkaProducerFactory;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import java.util.Collection;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.example.cli.StartRmqTask;
import org.example.cli.StopRmqTask;
import org.example.setup.managed.KafkaManager;
import org.example.resources.AsyncMsgResource;
import org.example.resources.HelloWorldResource;
import org.example.setup.configs.DwRefConfiguration;
import org.example.setup.filters.MDCRequestIdFilter;
import org.example.setup.managed.RmqManager;

public class DwRefApplication extends Application<DwRefConfiguration> {

  public static void main(final String[] args) throws Exception {
    new DwRefApplication().run(args);
  }

  @Override
  public String getName() {
    return "DwSampleRef";
  }

  @Override
  public void initialize(final Bootstrap<DwRefConfiguration> bootstrap) {
    bootstrap.addBundle(new MultiPartBundle());
    bootstrap.addBundle(swaggerBundle);
    bootstrap.addBundle(kafkaProducerBundle);
    bootstrap.addBundle(kafkaConsumerBundle);
  }

  private static final SwaggerBundle<DwRefConfiguration> swaggerBundle = new SwaggerBundle<>() {
    @Override
    protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(DwRefConfiguration config) {
      return config.getSwaggerBundleConfiguration();
    }
  };

  private static final KafkaProducerBundle<String, String, DwRefConfiguration> kafkaProducerBundle =
      new KafkaProducerBundle<>(List.of()) {
        @Override
        public KafkaProducerFactory<String, String> getKafkaProducerFactory(DwRefConfiguration configuration) {
          return configuration.getKafkaProducerFactory();
        }
      };

  static final ConsumerRebalanceListener NO_OP_CONSUMER_REBALANCE_LISTENER = new ConsumerRebalanceListener() {
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
    }
  };

  private static final KafkaConsumerBundle<String, String, DwRefConfiguration> kafkaConsumerBundle =
      new KafkaConsumerBundle<>(List.of(), NO_OP_CONSUMER_REBALANCE_LISTENER) {
        @Override
        public KafkaConsumerFactory<String, String> getKafkaConsumerFactory(DwRefConfiguration configuration) {
          return configuration.getKafkaConsumerFactory();
        }
      };

  @Override
  public void run(final DwRefConfiguration configuration,
      final Environment environment) {
    RmqManager rmqManager = new RmqManager(configuration.getRmqConfig());
    final KafkaManager kafkaManager = new KafkaManager(kafkaConsumerBundle.getConsumer(), kafkaProducerBundle.getProducer());
    environment.lifecycle().manage(kafkaManager);
    environment.lifecycle().manage(rmqManager);

    HelloWorldResource helloWorldResource = new HelloWorldResource();
    AsyncMsgResource asyncMsgResource = new AsyncMsgResource(rmqManager, kafkaManager);

    environment.jersey().register(helloWorldResource);
    environment.jersey().register(asyncMsgResource);

    environment.servlets().addFilter("MDCRequestIdFilter", new MDCRequestIdFilter())
        .addMappingForUrlPatterns(null, true, "/*");

    environment.admin().addTask(new StopRmqTask(rmqManager));
    environment.admin().addTask(new StartRmqTask(rmqManager));
  }
}
