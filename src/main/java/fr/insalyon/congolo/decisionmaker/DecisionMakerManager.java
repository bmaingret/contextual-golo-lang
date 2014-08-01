package fr.insalyon.congolo.decisionmaker;

public class DecisionMakerManager {
  private DecisionMakerManager() {
  }

  public static DecisionMaker getDecisionMaker() {
    return DecisionMakerHolder.dm;
  }

  public static DecisionMaker setDecisionMaker(DecisionMaker dm) {
    return DecisionMakerHolder.dm = dm;
  }

  private static class DecisionMakerHolder {
    private static DecisionMaker dm;
  }
}
