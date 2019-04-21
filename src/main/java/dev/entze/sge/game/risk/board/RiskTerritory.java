package dev.entze.sge.game.risk.board;

import java.util.Objects;

public class RiskTerritory {

  private final int territoryId;
  private final int continentId;

  private int occupantPlayerId;
  private int troops;

  public RiskTerritory(int territoryId, int continentId) {
    this(territoryId, continentId, -1, 0);
  }

  public RiskTerritory(int territoryId, int continentId, int occupantPlayerId, int troops) {
    this.territoryId = territoryId;
    this.continentId = continentId;
    this.occupantPlayerId = occupantPlayerId;
    this.troops = troops;
  }

  public int getTerritoryId() {
    return territoryId;
  }

  public int getContinentId() {
    return continentId;
  }

  public int getOccupantPlayerId() {
    return occupantPlayerId;
  }

  public void setOccupantPlayerId(int occupantPlayerId) {
    this.occupantPlayerId = occupantPlayerId;
  }

  public int getTroops() {
    return troops;
  }

  public void setTroops(int troops) {
    this.troops = troops;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskTerritory that = (RiskTerritory) o;
    return territoryId == that.territoryId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(territoryId);
  }
}
