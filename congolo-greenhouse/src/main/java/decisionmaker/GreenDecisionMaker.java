package decisionmaker;

import fr.insalyon.congolo.decisionmaker.DefaultDecisionMaker;
import fr.insalyon.congolo.event.Event;
import fr.insalyon.congolo.context.Context;
import fr.insalyon.congolo.context.ContextManager;
import fr.insalyon.congolo.ContextualFunctionProxy;
import fr.insalyon.congolo.context.ConcreteValueContext;
import fr.insalyon.congolo.event.FunctionInvocationEvent;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class GreenDecisionMaker extends DefaultDecisionMaker {

@Override
  public void decide(Event event) {
    Object source = event.getSource();
    List<Context> contexts = ContextManager.INSTANCE.getContexts(((Class)source).getSimpleName());

    Context activatedContext = null;
    String activatedContextName = null;
    for (Context context : contexts) {
      Object contextValue = ((ConcreteValueContext) context).getValue();
      if ((Integer) contextValue > 10) {
        activatedContextName = "TemperatureTooHot";
      } else {
        activatedContextName = "TemperatureTooCold";
      }
    }

    MethodHandle handle = findHandle(
        (Class) source,
        ((FunctionInvocationEvent) event).getMethod() + ContextualFunctionProxy.CONTEXTSUFFIX + activatedContextName,
        ((FunctionInvocationEvent) event).getParameters());

    ((FunctionInvocationEvent) event).setHandle(handle);
    this.proxyTopic.send(event);
  }
}
