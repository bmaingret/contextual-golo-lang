package fr.insalyon.congolo.decisionmaker;

import fr.insalyon.congolo.event.Event;

public interface DecisionMaker {

  public void init();

  public void train();

  public Event decide(Event event);
}
