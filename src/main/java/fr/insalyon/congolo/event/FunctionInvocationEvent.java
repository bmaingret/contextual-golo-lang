package fr.insalyon.congolo.event;

import fr.insalyon.congolo.context.Context;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionInvocationEvent extends AbstractEvent implements ActionEvent {
  private final Object[] parameters;
  private final String method;
  private MethodHandle handle;

  public FunctionInvocationEvent(Object source, String method, Object[] parameters) {
    super(source);
    this.method = method;
    this.parameters = parameters;
  }

  public MethodHandle getHandle() {
    return handle;
  }

  public void setHandle(MethodHandle handle) {
    this.handle = handle;
  }

  @Override
  public Object getSource() {
    return super.getSource();
  }

  public Object[] getParameters() {
    return parameters;
  }

  public String getMethod() {
    return method;
  }

  @Override
  public String toString() {
    return super.toString()
        + ", method " + this.getMethod()
        + ", paramters " + this.getParameters()
        + ((null == handle) ? "" : "handle " + this.getHandle());
  }
}
