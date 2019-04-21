package dev.entze.sge.game.risk.RiskConfiguration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class RiskConfiguration {

  public static final RiskConfiguration RISK_DEFAULT_CONFIG = new RiskConfiguration(6, 3, 2,
      new int[] {50, 35, 30, 25, 20}, true, 3, 2, true,
      RiskMissionConfiguration.liberatePlayer(0, 6), Arrays.asList(), Arrays.asList(), "");

  private int maxNumberOfPlayers = 2;
  private int maxAttackerDice = 3;
  private int maxDefenderDice = 2;
  private int[] initialTroops = null;
  private boolean withCards = true;
  private int cardTypes = 3;
  private int numberOfJokers = 2;
  private boolean withMissions = true;
  private List<RiskMissionConfiguration> missions;
  private List<RiskContinentConfiguration> continents;
  private List<RiskTerritoryConfiguration> territories;
  private String map;

  public RiskConfiguration(int maxNumberOfPlayers, int maxAttackerDice, int maxDefenderDice,
      int[] initialTroops, boolean withCards, int cardTypes, int numberOfJokers,
      boolean withMissions,
      List<RiskMissionConfiguration> missions,
      List<RiskContinentConfiguration> continents,
      List<RiskTerritoryConfiguration> territories, String map) {
    this.maxNumberOfPlayers = maxNumberOfPlayers;
    this.maxAttackerDice = maxAttackerDice;
    this.maxDefenderDice = maxDefenderDice;
    this.initialTroops = initialTroops;
    this.withCards = withCards;
    this.cardTypes = cardTypes;
    this.numberOfJokers = numberOfJokers;
    this.withMissions = withMissions;
    this.missions = missions;
    this.continents = continents;
    this.territories = territories;
    this.map = map;
  }

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
      for (int i = 0; i < initialTroops.length; i++) {
        initialTroops[i] = territoryNumber;
        do {
          territoryNumber -= steps;
        } while (initialTroops[i] * (i + 2) < territoryNumber * (i + 3) - (steps * 3));
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

  public boolean isWithMissions() {
    return withMissions;
  }

  public void setWithMissions(boolean withMissions) {
    this.withMissions = withMissions;
  }

  public List<RiskMissionConfiguration> getMissions() {
    return missions;
  }

  public void setMissions(
      List<RiskMissionConfiguration> missions) {
    this.missions = missions;
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
}