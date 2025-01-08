package org.example.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.statemachine.StateMachineFactory.ApplicationEvents;
import org.example.core.statemachine.StateMachineFactory.ApplicationStates;
import org.springframework.statemachine.StateMachine;

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
    stateMachine.sendEvent(event);
    return Response.ok(stateMachine.getState().getIds()).build();
  }

}
