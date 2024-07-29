package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Getter;

@Getter
public class DwRefConfiguration extends Configuration {

  @JsonProperty("swagger")
  private final SwaggerBundleConfiguration swaggerBundleConfiguration = new SwaggerBundleConfiguration();
}
