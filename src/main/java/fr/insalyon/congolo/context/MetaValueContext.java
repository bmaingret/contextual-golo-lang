package fr.insalyon.congolo.context;

public class MetaValueContext implements Context {
  private String value = null;

  public MetaValueContext() {
  }

  public MetaValueContext(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    return "MetaValueContext: " +
        "value = " + this.value;
  }
}
