package client;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.core.setup.Environment;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.setup.configs.DwRefConfiguration;

@Slf4j
public class CatFactClient {

  public static final String CAT_FACT_CLIENT_NAME = "cat-fact-client";
  private static final String CAT_FACT_ENDPOINT = "https://catfact.ninja/fact";
  private final Client client;

  public CatFactClient(Environment env, DwRefConfiguration config) {
    this.client = new JerseyClientBuilder(env)
        .using(config.getJerseyClientConfiguration())
        .build(CAT_FACT_CLIENT_NAME);
  }

  public String getCatFact() {
    WebTarget target = client.target(CAT_FACT_ENDPOINT);
    Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

    Response response = builder.get();
    log.info("Cat fact response :: {}", response);

    if (response.getStatus() == 200) {
      return response.readEntity(String.class);
    } else {
      throw new RuntimeException("Failed to fetch cat fact, status: " + response.getStatus());
    }
  }
}