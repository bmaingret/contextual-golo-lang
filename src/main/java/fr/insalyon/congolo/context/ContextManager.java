package fr.insalyon.congolo.context;

import java.util.*;

public class ContextManager {
  public final static ContextManager INSTANCE = new ContextManager();
  private final Map<String, List<Context>> contexts = new HashMap<>();

  private ContextManager() {
  }

  public void register(String module, Context context) {
    this.addContext(module, context);
  }

  private void addContext(String module, Context context) {
    this.contexts.putIfAbsent(module, new LinkedList<Context>());
    this.contexts.get(module).add(context);
  }

  public List<Context> getContexts(String module) {
    return contexts.get(module);
  }
}
