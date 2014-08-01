package fr.insalyon.congolo;

import fr.insalyon.congolo.context.Context;
import fr.insalyon.congolo.decisionmaker.DecisionMaker;
import fr.insalyon.congolo.decisionmaker.DecisionMakerManager;
import fr.insalyon.congolo.event.EnvironmentProvider;
import fr.insalyon.congolo.event.Event;
import fr.insalyon.congolo.event.FunctionInvocationEvent;
import gololang.concurrent.messaging.MessagingEnvironment;
import gololang.concurrent.messaging.Topic;

import java.util.Arrays;
import java.util.LinkedList;

public class ContextualFunctionProxy {
  public static final String DM_TOPIC = "congolo:internal:decision";
  public static final String PROXY_TOPIC = "congolo:internal:proxy";
  public static final String CONTEXTSUFFIX = "__$context$__";
  private static final MessagingEnvironment env = EnvironmentProvider.getEnvironment();

  public static Object invoke(Class source, String methodName, Object[] parameters) {
    Event event = new FunctionInvocationEvent(
        source,
        methodName,
        parameters);
    Topic topic = env.topic(DM_TOPIC);
    topic.send(event);

    return null;
  }
}
