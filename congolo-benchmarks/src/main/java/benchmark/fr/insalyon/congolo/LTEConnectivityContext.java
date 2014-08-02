package benchmark.fr.insalyon.congolo;

import fr.insalyon.congolo.context.*;

public class LTEConnectivityContext implements ConcreteValueContext {
  private Object value;

  public LTEConnectivityContext(Object value){
    this.value = value;
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