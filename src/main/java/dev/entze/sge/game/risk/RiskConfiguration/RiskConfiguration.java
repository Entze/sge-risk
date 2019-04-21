package dev.entze.sge.game.risk.RiskConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class RiskConfiguration {

  private int maxNumberOfPlayers = 2;
  private int[] initialTroops = null;
  private boolean withCards = true;
  private int cardTypes = 3;
  private int numberOfJokers = 2;
  private List<RiskContinentConfiguration> continents;
  private List<RiskTerritoryConfiguration> territories;
  private String map;

  public int getMaxNumberOfPlayers() {
    return maxNumberOfPlayers;
  }

  public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
    this.maxNumberOfPlayers = maxNumberOfPlayers;
  }

  public int[] getInitialTroops() {
    if (initialTroops == null && territories != null) {
      initialTroops = new int[maxNumberOfPlayers - 1];
      int territoryNumber = BigDecimal.valueOf(territories.size()).setScale(-1, RoundingMode.UP)
          .intValue();
      int steps = Math.max(1, territoryNumber / 10);
      territoryNumber *= 2;
      for (int i = 0; i < initialTroops.length; i++) {
        initialTroops[i] = territoryNumber / (i + 2);
        do {
          territoryNumber += steps;
        } while ((territoryNumber / (i + 3)) * (i + 3) != territoryNumber);
      }
    }
    return initialTroops;
  }

  public void setInitialTroops(int... initialTroops) {
    for (int i = 0; i < initialTroops.length && i < this.initialTroops.length; i++) {
      this.initialTroops[i] = initialTroops[i];
    }
  }

  public void setInitialTroops(int initialTroops) {
    getInitialTroops();
    if (this.initialTroops != null) {
      Arrays.fill(this.initialTroops, initialTroops);
    }
  }

  public boolean isWithCards() {
    return withCards;
  }

  public void setWithCards(boolean withCards) {
    this.withCards = withCards;
  }

  public int getCardTypes() {
    return cardTypes;
  }

  public void setCardTypes(int cardTypes) {
    this.cardTypes = cardTypes;
  }

  public int getNumberOfJokers() {
    return numberOfJokers;
  }

  public void setNumberOfJokers(int numberOfJokers) {
    this.numberOfJokers = numberOfJokers;
  }

  public List<RiskContinentConfiguration> getContinents() {
    return continents;
  }

  public void setContinents(List<RiskContinentConfiguration> continents) {
    this.continents = continents;
  }

  public List<RiskTerritoryConfiguration> getTerritories() {
    return territories;
  }

  public void setTerritories(
      List<RiskTerritoryConfiguration> territories) {
    this.territories = territories;
  }

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public static Yaml getYaml() {
    Constructor constructor = new Constructor(RiskConfiguration.class);
    TypeDescription riskConfigurationDescription = new TypeDescription(RiskConfiguration.class);
    riskConfigurationDescription
        .addPropertyParameters("continents", RiskContinentConfiguration.class);
    riskConfigurationDescription
        .addPropertyParameters("territories", RiskTerritoryConfiguration.class);
    constructor.addTypeDescription(riskConfigurationDescription);
    return new Yaml(constructor);
  }

  private class RiskContinentConfiguration {

    private int continentId;
    private int troopBonus;


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

  private class RiskTerritoryConfiguration {

    private int territoryId;
    private int cardType;
    private Set<Integer> connects;

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
}