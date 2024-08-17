package org.example.setup.configs;

import lombok.Getter;

@Getter
public class RmqConfig {

  private String uri;
  private String prefix;
  private String queueName;
  private int concurrencyCount = 1;
  private int prefetchCount = 1;
}
