package fr.insalyon.congolo.decisionmaker;

import fr.insalyon.congolo.ContextualFunctionProxy;
import fr.insalyon.congolo.context.ConcreteValueContext;
import fr.insalyon.congolo.context.Context;
import fr.insalyon.congolo.context.ContextManager;
import fr.insalyon.congolo.event.Event;
import fr.insalyon.congolo.event.FunctionInvocationEvent;
import gololang.concurrent.messaging.MessagingEnvironment;
import gololang.concurrent.messaging.MessagingFunction;
import gololang.concurrent.messaging.Topic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

public class DefaultDecisionMaker implements DecisionMaker, MessagingFunction {
  protected final MessagingEnvironment env;
  protected final Topic dmTopic;
  protected final Topic proxyTopic;


  public DefaultDecisionMaker() {
    this.init();
    this.env = fr.insalyon.congolo.event.EnvironmentProvider.getEnvironment();
    this.dmTopic = env.topic(ContextualFunctionProxy.DM_TOPIC);
    this.dmTopic.registerListener(this);
    this.proxyTopic = env.topic(ContextualFunctionProxy.PROXY_TOPIC);
  }

  @Override
  public void apply(Object message) {
    this.decide((Event) message);
  }

  @Override
  public void init() {
  }

  @Override
  public void train() {
  }

  @Override
  public void decide(Event event) {
    Object source = event.getSource();
    List<Context> contexts = ContextManager.INSTANCE.getContexts(((Class)source).getSimpleName());

    Context activatedContext = null;
    for (Context context : contexts) {
      Object contextValue = ((ConcreteValueContext) context).getValue();
      if ((Boolean) contextValue) {
        activatedContext = context;
      }
    }

    String activatedContextName;
    if (null != activatedContext) {
      activatedContextName = activatedContext.getClass().getSimpleName();
    } else {
      activatedContextName = "LTEConnectivityContext";
    }

    MethodHandle handle = findHandle(
        (Class) source,
        ((FunctionInvocationEvent) event).getMethod() + ContextualFunctionProxy.CONTEXTSUFFIX + activatedContextName,
        ((FunctionInvocationEvent) event).getParameters());

    ((FunctionInvocationEvent) event).setHandle(handle);
    this.proxyTopic.send(event);
  }

  protected MethodHandle findHandle(Class target, String methodName, Object[] parameters) {
    MethodType methodType;
    MethodHandle methodHandle = null;
    MethodHandles.Lookup lookup = MethodHandles.lookup();

    methodType = MethodType.genericMethodType(parameters.length);
    try {
      methodHandle = lookup.findStatic(target, methodName, methodType);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return methodHandle;
  }
}
