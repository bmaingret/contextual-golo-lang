package fr.insalyon.congolo.context;

import fr.insalyon.congolo.event.ActionEvent;

public interface ConcreteValueContext extends Context {
  public Object getValue();

  public void setValue(Object value);
}
