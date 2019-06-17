package dev.entze.sge.game.risk.board;

public class RiskContinent {

  private final int troopBonus;

  public RiskContinent(int troopBonus) {
    this.troopBonus = troopBonus;
  }

  public RiskContinent(RiskContinent riskContinent) {
    this.troopBonus = riskContinent.troopBonus;
  }

  public int getTroopBonus() {
    return troopBonus;
  }


}
