package fr.insalyon.congolo.event;

public class ContextDataEvent extends AbstractEvent implements DataEvent {
  public ContextDataEvent(Object source) {
    super(source);
  }

  @Override
  public Object getValue() {
    return null;
  }
}
