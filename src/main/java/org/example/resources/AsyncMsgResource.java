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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.setup.managed.RmqManager;

@Path("/async")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Async Messaging")
@Slf4j
@RequiredArgsConstructor
public class AsyncMsgResource {

  private final RmqManager rmqManager;
  private final Producer<String, String> kafkaProducer;

  @POST
  @Path("/rmq")
  @Consumes(MediaType.TEXT_PLAIN)
  @RequestBody
  public Response publishRmqMsg(String msg, @QueryParam("routingKey") String rk) {
    rmqManager.getRmqProducer().publish(rk, msg.getBytes(StandardCharsets.UTF_8));
    return Response.ok(msg).build();
  }

  @POST
  @Path("/kafka")
  @Consumes(MediaType.TEXT_PLAIN)
  @RequestBody
  public Response publishKafkaMsg(String msg, @QueryParam("topic") String topic) {
    String key = UUID.randomUUID().toString();
    kafkaProducer.send(new ProducerRecord<>(topic, key, msg));
    return Response.ok(msg).build();
  }

}
