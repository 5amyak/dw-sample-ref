package org.example.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import org.example.DwRefApplication;
import org.example.setup.configs.DwRefConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// mocking not allowed, test infra needs to spin up using docker container
@ExtendWith(DropwizardExtensionsSupport.class)
public class AsyncMsgResourceIT {

  private final DropwizardAppExtension<DwRefConfiguration> EXT = new DropwizardAppExtension<>(
      DwRefApplication.class,
      ResourceHelpers.resourceFilePath("test-config.yml")
  );

  @Test
  public void testPublishMsg() {
    // Arrange
    String msg = "Hello, World!";
    String rk = "test-routing-key";

    // Act
    Client client = EXT.client();
    Response response = client.target(
            String.format("http://localhost:%d/async/rmq", EXT.getLocalPort()))
        .queryParam("routingKey", rk)
        .request()
        .post(Entity.text(msg));

    // Assert
    assertEquals(200, response.getStatus());
    assertEquals(msg, response.readEntity(String.class));
  }
}