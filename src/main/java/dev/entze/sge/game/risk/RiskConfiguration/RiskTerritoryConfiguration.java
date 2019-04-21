package dev.entze.sge.game.risk.RiskConfiguration;

import java.util.Collections;
import java.util.Set;

public class RiskTerritoryConfiguration {



  private int territoryId;
  private int cardType;
  private Set<Integer> connects;

  public RiskTerritoryConfiguration() {
  }

  public RiskTerritoryConfiguration(int territoryId, int cardType) {
    this(territoryId, cardType, Collections.emptySet());
  }

  public RiskTerritoryConfiguration(int territoryId, int cardType,
      Set<Integer> connects) {
    this.territoryId = territoryId;
    this.cardType = cardType;
    this.connects = connects;
  }

  public int getTerritoryId() {
    return territoryId;
  }

  public void setTerritoryId(int territoryId) {
    this.territoryId = territoryId;
  }

  public int getCardType() {
    return cardType;
  }

  public void setCardType(int cardType) {
    this.cardType = cardType;
  }

  public Set<Integer> getConnects() {
    return connects;
  }

  public void setConnects(Set<Integer> connects) {
    this.connects = connects;
  }

}
