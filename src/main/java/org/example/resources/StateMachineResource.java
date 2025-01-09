package org.example.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.statemachine.StateMachineFactory.ApplicationEvents;
import org.example.core.statemachine.StateMachineFactory.ApplicationStates;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.ObjectStateMachine;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Path("/sm")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Spring State Machine")
@Slf4j
@RequiredArgsConstructor
public class StateMachineResource {

  private final StateMachine<ApplicationStates, ApplicationEvents> stateMachine;

  @GET
  public Response fetchStatus() {
    return Response.ok(stateMachine.getExtendedState() + "\n"
        + stateMachine.getState().getId()).build();
  }

  @POST
  @Path("/publish")
  public Response publishEvent(@QueryParam("event") ApplicationEvents event) {
    Flux<StateMachineEventResult<ApplicationStates, ApplicationEvents>> flux = stateMachine
        .sendEvent(Mono.just(MessageBuilder.withPayload(event).build()));

    ObjectStateMachine<ApplicationStates, ApplicationEvents> objectStateMachine =
        (ObjectStateMachine<ApplicationStates, ApplicationEvents>) (Objects.requireNonNull(flux.single().block()).getRegion());
    return Response.ok(objectStateMachine.getState().getId()).build();
  }

}
