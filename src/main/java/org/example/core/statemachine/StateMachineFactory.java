package org.example.core.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;

@Slf4j
public class StateMachineFactory {

  private static final int APPROVAL_THRESHOLD = 2;
  private static final String APPROVAL_COUNT_FIELD = "approvalCount";

  public static StateMachine<ApplicationStates, ApplicationEvents> buildStateMachine() throws Exception {
    StateMachineBuilder.Builder<ApplicationStates, ApplicationEvents> builder = StateMachineBuilder.builder();
    builder.configureStates().withStates()
        .initial(ApplicationStates.IN_REVIEW)
        .junction(ApplicationStates.PENDING)
        .end(ApplicationStates.APPROVED)
        .end(ApplicationStates.REJECTED);

    builder.configureTransitions()
        .withExternal()
        .source(ApplicationStates.IN_REVIEW)
        .target(ApplicationStates.PENDING)
        .action(approvalCountIncrementAction())
        .event(ApplicationEvents.APPROVE)
        .and().withJunction()
        .source(ApplicationStates.PENDING)
        .first(ApplicationStates.APPROVED, approvalCountGuard())
        .last(ApplicationStates.IN_REVIEW)
        .and().withExternal()
        .source(ApplicationStates.IN_REVIEW)
        .target(ApplicationStates.REJECTED)
        .event(ApplicationEvents.REJECT);

    return builder.build();
  }

  public static Guard<ApplicationStates, ApplicationEvents> approvalCountGuard() {
    return ctx -> (int) ctx.getExtendedState()
        .getVariables()
        .getOrDefault(APPROVAL_COUNT_FIELD, 0) > APPROVAL_THRESHOLD;
  }

  public static Action<ApplicationStates, ApplicationEvents> approvalCountIncrementAction() {
    return ctx -> {
      int approvals = (int) ctx.getExtendedState().getVariables()
          .getOrDefault(APPROVAL_COUNT_FIELD, 0);
      approvals++;
      ctx.getExtendedState().getVariables()
          .put(APPROVAL_COUNT_FIELD, approvals);
    };
  }

  public enum ApplicationStates {
    IN_REVIEW, APPROVED, REJECTED, PENDING
  }

  public enum ApplicationEvents {
    APPROVE, REJECT
  }
}
