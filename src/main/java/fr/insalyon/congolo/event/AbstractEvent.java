package fr.insalyon.congolo.event;

public abstract class AbstractEvent implements Event {
  private final Object source;

  AbstractEvent(Object source) {
    this.source = source;
  }

  @Override
  public Object getSource() {
    return this.source;
  }

  @Override
  public String toString() {
    return "Event: source=" + this.getSource();
  }
}
