package context;

import fr.insalyon.congolo.context.ConcreteValueContext;

public class TemperatureContext implements ConcreteValueContext {
  private Object value;

  public TemperatureContext(Object value){
    this.value = value;
  }

  public TemperatureContext(){
  }

  @Override
  public Object getValue() {
    return this.value;
  }

  @Override
  public void setValue(Object value){
    this.value = value;
  }
}