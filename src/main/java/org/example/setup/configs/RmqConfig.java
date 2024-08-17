package org.example.setup.configs;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class RmqConfig {

  private String uri;
  private String connName;
  private List<QueueConfig> queues = new ArrayList<>();
}
