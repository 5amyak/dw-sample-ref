package org.example.setup.configs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.kafka.KafkaConsumerFactory;
import io.dropwizard.kafka.KafkaProducerFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DwRefConfiguration extends Configuration {

  @JsonProperty("swagger")
  private final SwaggerBundleConfiguration swaggerBundleConfiguration = new SwaggerBundleConfiguration();

  @Valid
  @NotNull
  @JsonProperty("producer")
  private KafkaProducerFactory<String, String> kafkaProducerFactory;

  @Valid
  @NotNull
  @JsonProperty("consumer")
  private KafkaConsumerFactory<String, String> kafkaConsumerFactory;

  private RmqConfig rmqConfig;
}
