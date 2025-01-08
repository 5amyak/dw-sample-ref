package org.example.core.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.example.core.statemachine.StateMachineFactory.ApplicationEvents;
import org.example.core.statemachine.StateMachineFactory.ApplicationStates;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
public class StateMachineListener extends StateMachineListenerAdapter<ApplicationStates, ApplicationEvents> {

  @Override
  public void stateChanged(State from, State to) {
    log.info("Transitioned from {} to {}", from == null ?
        "none" : from.getId(), to.getId());
  }
}