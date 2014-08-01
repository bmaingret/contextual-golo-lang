package fr.insalyon.congolo.event;

import gololang.concurrent.messaging.MessagingEnvironment;

public final class EnvironmentProvider {
  private EnvironmentProvider() {
  }

  public static MessagingEnvironment getEnvironment() {
    return EnvironmentHolder.env;
  }

  private static class EnvironmentHolder {
    private final static MessagingEnvironment env = MessagingEnvironment.builder().withSingleThreadExecutor();
  }
}
