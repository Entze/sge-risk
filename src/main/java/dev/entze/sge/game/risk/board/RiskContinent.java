package dev.entze.sge.game.risk.board;

import java.util.Objects;

public class RiskContinent {

  private final int continentId;
  private final int troopBonus;

  public RiskContinent(int continentId, int troopBonus) {
    this.continentId = continentId;
    this.troopBonus = troopBonus;
  }

  public int getContinentId() {
    return continentId;
  }

  public int getTroopBonus() {
    return troopBonus;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskContinent that = (RiskContinent) o;
    return continentId == that.continentId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(continentId);
  }
}
