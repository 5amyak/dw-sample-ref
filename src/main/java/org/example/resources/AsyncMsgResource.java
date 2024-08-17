package org.example.resources;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.setup.managed.RmqManager;

@Path("/async")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Async Messaging")
@Slf4j
@RequiredArgsConstructor
public class AsyncMsgResource {

  private final RmqManager rmqManager;

  @POST
  @Path("/rmq")
  @Consumes(MediaType.TEXT_PLAIN)
  @RequestBody
  public Response publishMsg(String msg, @QueryParam("routingKey") String rk) {
    rmqManager.getRmqProducer().publish(rk, msg.getBytes(StandardCharsets.UTF_8));
    return Response.ok(msg).build();
  }

}
