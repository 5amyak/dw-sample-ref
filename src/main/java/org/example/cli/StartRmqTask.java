package org.example.cli;

import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.example.setup.managed.RmqManager;

public class StartRmqTask extends Task {

  private final RmqManager rmqManager;

  public StartRmqTask(RmqManager rmqManager) {
    super("start-rmq");
    this.rmqManager = rmqManager;
  }

  @Override
  public void execute(Map<String, List<String>> map, PrintWriter printWriter) throws Exception {
    this.rmqManager.startConsumers();
    printWriter.println("Successfully started RMQ consumers");
    printWriter.flush();
  }
}
