package org.example.setup.configs;

import lombok.Getter;

@Getter
public class QueueConfig {

  private String name;
  private String prefix;
  private int concurrencyCount = 1;
  private int prefetchCount = 1;
  private int maxLength = Integer.MAX_VALUE;
}
