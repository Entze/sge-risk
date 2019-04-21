package dev.entze.sge.game.risk.RiskConfiguration;

public class RiskContinentConfiguration {

  public static final RiskContinentConfiguration NORTH_AMERICA = new RiskContinentConfiguration(0,
      5);
  public static final RiskContinentConfiguration SOUTH_AMERICA = new RiskContinentConfiguration(1,
      2);
  public static final RiskContinentConfiguration EUROPA = new RiskContinentConfiguration(2, 5);
  public static final RiskContinentConfiguration AFRICA = new RiskContinentConfiguration(3, 3);
  public static final RiskContinentConfiguration ASIA = new RiskContinentConfiguration(4, 7);
  public static final RiskContinentConfiguration AUSTRALIA = new RiskContinentConfiguration(5, 2);

  private int continentId;
  private int troopBonus;

  public RiskContinentConfiguration() {
  }

  public RiskContinentConfiguration(int continentId, int troopBonus) {
    this.continentId = continentId;
    this.troopBonus = troopBonus;
  }

  public int getContinentId() {
    return continentId;
  }

  public void setContinentId(int continentId) {
    this.continentId = continentId;
  }

  public int getTroopBonus() {
    return troopBonus;
  }

  public void setTroopBonus(int troopBonus) {
    this.troopBonus = troopBonus;
  }


}
