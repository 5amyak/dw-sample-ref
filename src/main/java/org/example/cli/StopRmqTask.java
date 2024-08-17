package org.example.cli;

import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.example.setup.managed.RmqManager;


public class StopRmqTask extends Task {

  private final RmqManager rmqManager;

  public StopRmqTask(RmqManager rmqManager) {
    super("stop-rmq");
    this.rmqManager = rmqManager;
  }

  @Override
  public void execute(Map<String, List<String>> map, PrintWriter printWriter) throws Exception {
    this.rmqManager.stopConsumers();
    printWriter.println("Successfully stopped RMQ consumers");
    printWriter.flush();
  }
}
