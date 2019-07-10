package dev.entze.sge.game.risk.configuration;

import static dev.entze.sge.game.risk.board.RiskCard.ARTILLERY;
import static dev.entze.sge.game.risk.board.RiskCard.CAVALRY;
import static dev.entze.sge.game.risk.board.RiskCard.INFANTRY;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.AFRICA;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.ASIA;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.AUSTRALIA;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.EUROPE;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.NORTH_AMERICA;
import static dev.entze.sge.game.risk.configuration.RiskContinentConfiguration.SOUTH_AMERICA;

import dev.entze.sge.game.risk.board.RiskTerritory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class RiskTerritoryConfiguration {


  public static final RiskTerritoryConfiguration ALASKA = new RiskTerritoryConfiguration(
      ARTILLERY,
      NORTH_AMERICA);
  public static final RiskTerritoryConfiguration ALBERTA = new RiskTerritoryConfiguration(
      ARTILLERY,
      NORTH_AMERICA,
      ALASKA);
  public static final RiskTerritoryConfiguration CENTRAL_AMERICA = new RiskTerritoryConfiguration(
      INFANTRY,
      NORTH_AMERICA);
  public static final RiskTerritoryConfiguration EASTERN_UNITED_STATES = new RiskTerritoryConfiguration(
      INFANTRY,
      NORTH_AMERICA,
      CENTRAL_AMERICA);
  public static final RiskTerritoryConfiguration GREENLAND = new RiskTerritoryConfiguration(
      CAVALRY,
      NORTH_AMERICA);
  public static final RiskTerritoryConfiguration NORTHWEST_TERRITORY = new RiskTerritoryConfiguration(
      CAVALRY,
      NORTH_AMERICA,
      ALASKA,
      ALBERTA,
      GREENLAND);
  public static final RiskTerritoryConfiguration ONTARIO = new RiskTerritoryConfiguration(
      ARTILLERY,
      NORTH_AMERICA,
      ALBERTA,
      GREENLAND,
      EASTERN_UNITED_STATES,
      NORTHWEST_TERRITORY);
  public static final RiskTerritoryConfiguration QUEBEC = new RiskTerritoryConfiguration(
      ARTILLERY,
      NORTH_AMERICA,
      EASTERN_UNITED_STATES,
      GREENLAND,
      NORTHWEST_TERRITORY,
      ONTARIO);
  public static final RiskTerritoryConfiguration WESTERN_UNITED_STATES = new RiskTerritoryConfiguration(
      ARTILLERY,
      NORTH_AMERICA,
      ALBERTA,
      CENTRAL_AMERICA,
      EASTERN_UNITED_STATES,
      ONTARIO);
  //SOUTH_AMERICA
  public static final RiskTerritoryConfiguration ARGENTINA = new RiskTerritoryConfiguration(
      INFANTRY,
      SOUTH_AMERICA);
  public static final RiskTerritoryConfiguration BRAZIL = new RiskTerritoryConfiguration(
      INFANTRY,
      SOUTH_AMERICA,
      ARGENTINA);
  public static final RiskTerritoryConfiguration PERU = new RiskTerritoryConfiguration(
      CAVALRY,
      SOUTH_AMERICA,
      ARGENTINA,
      BRAZIL);
  public static final RiskTerritoryConfiguration VENEZUELA = new RiskTerritoryConfiguration(
      CAVALRY,
      SOUTH_AMERICA,
      CENTRAL_AMERICA,
      BRAZIL,
      PERU);
  //EUROPE
  public static final RiskTerritoryConfiguration GREAT_BRITAIN = new RiskTerritoryConfiguration(
      INFANTRY,
      EUROPE);
  public static final RiskTerritoryConfiguration ICELAND = new RiskTerritoryConfiguration(
      CAVALRY,
      EUROPE,
      GREENLAND,
      GREAT_BRITAIN);
  public static final RiskTerritoryConfiguration NORTHERN_EUROPE = new RiskTerritoryConfiguration(
      CAVALRY,
      EUROPE,
      GREAT_BRITAIN);
  public static final RiskTerritoryConfiguration SCANDINAVIA = new RiskTerritoryConfiguration(
      INFANTRY,
      EUROPE,
      GREAT_BRITAIN,
      ICELAND,
      NORTHERN_EUROPE);
  public static final RiskTerritoryConfiguration SOUTHERN_EUROPE = new RiskTerritoryConfiguration(
      INFANTRY,
      EUROPE,
      NORTHERN_EUROPE);
  public static final RiskTerritoryConfiguration UKRAINE = new RiskTerritoryConfiguration(
      INFANTRY,
      EUROPE,
      NORTHERN_EUROPE,
      SCANDINAVIA,
      SOUTHERN_EUROPE);
  public static final RiskTerritoryConfiguration WESTERN_EUROPE = new RiskTerritoryConfiguration(
      INFANTRY,
      EUROPE,
      GREAT_BRITAIN,
      NORTHERN_EUROPE,
      SOUTHERN_EUROPE);
  //AFRICA
  public static final RiskTerritoryConfiguration CENTRAL_AFRICA = new RiskTerritoryConfiguration(
      ARTILLERY,
      AFRICA);
  public static final RiskTerritoryConfiguration EAST_AFRICA = new RiskTerritoryConfiguration(
      INFANTRY,
      AFRICA,
      CENTRAL_AFRICA);
  public static final RiskTerritoryConfiguration EGYPT = new RiskTerritoryConfiguration(
      CAVALRY,
      AFRICA,
      SOUTHERN_EUROPE,
      EAST_AFRICA);
  public static final RiskTerritoryConfiguration MADAGASCAR = new RiskTerritoryConfiguration(
      CAVALRY,
      AFRICA,
      EAST_AFRICA);
  public static final RiskTerritoryConfiguration NORTH_AFRICA = new RiskTerritoryConfiguration(
      INFANTRY,
      AFRICA,
      BRAZIL,
      SOUTHERN_EUROPE,
      WESTERN_EUROPE,
      CENTRAL_AFRICA,
      EGYPT);
  public static final RiskTerritoryConfiguration SOUTH_AFRICA = new RiskTerritoryConfiguration(
      ARTILLERY,
      AFRICA,
      CENTRAL_AFRICA,
      EAST_AFRICA,
      MADAGASCAR);
  //ASIA
  public static final RiskTerritoryConfiguration AFGHANISTAN = new RiskTerritoryConfiguration(
      INFANTRY,
      ASIA,
      UKRAINE);
  public static final RiskTerritoryConfiguration CHINA = new RiskTerritoryConfiguration(
      ARTILLERY,
      ASIA,
      AFGHANISTAN);
  public static final RiskTerritoryConfiguration INDIA = new RiskTerritoryConfiguration(
      CAVALRY,
      ASIA,
      AFGHANISTAN,
      CHINA);
  public static final RiskTerritoryConfiguration IRKUTSK = new RiskTerritoryConfiguration(
      ARTILLERY,
      ASIA);
  public static final RiskTerritoryConfiguration JAPAN = new RiskTerritoryConfiguration(
      CAVALRY,
      ASIA);
  public static final RiskTerritoryConfiguration KAMCHATKA = new RiskTerritoryConfiguration(
      ARTILLERY,
      ASIA,
      ALASKA,
      IRKUTSK,
      JAPAN);
  public static final RiskTerritoryConfiguration MIDDLE_EAST = new RiskTerritoryConfiguration(
      ARTILLERY,
      ASIA,
      SOUTHERN_EUROPE,
      UKRAINE,
      EAST_AFRICA,
      EGYPT,
      AFGHANISTAN,
      INDIA);
  public static final RiskTerritoryConfiguration MONGOLIA = new RiskTerritoryConfiguration(
      CAVALRY,
      ASIA,
      CHINA,
      IRKUTSK,
      JAPAN,
      KAMCHATKA);
  public static final RiskTerritoryConfiguration SIAM = new RiskTerritoryConfiguration(
      CAVALRY,
      ASIA,
      CHINA,
      INDIA);
  public static final RiskTerritoryConfiguration SIBERIA = new RiskTerritoryConfiguration(
      INFANTRY,
      ASIA,
      CHINA,
      IRKUTSK,
      MONGOLIA);
  public static final RiskTerritoryConfiguration URAL = new RiskTerritoryConfiguration(
      INFANTRY,
      ASIA,
      UKRAINE,
      AFGHANISTAN,
      CHINA,
      SIBERIA);
  public static final RiskTerritoryConfiguration YAKUTSK = new RiskTerritoryConfiguration(
      ARTILLERY,
      ASIA,
      IRKUTSK,
      KAMCHATKA,
      SIBERIA);
  //AUSTRALIA
  public static final RiskTerritoryConfiguration EASTERN_AUSTRALIA = new RiskTerritoryConfiguration(
      CAVALRY,
      AUSTRALIA);
  public static final RiskTerritoryConfiguration INDONESIA = new RiskTerritoryConfiguration(
      INFANTRY,
      AUSTRALIA,
      SIAM);
  public static final RiskTerritoryConfiguration NEW_GUINEA = new RiskTerritoryConfiguration(
      CAVALRY,
      AUSTRALIA,
      EASTERN_AUSTRALIA,
      INDONESIA);
  public static final RiskTerritoryConfiguration WESTERN_AUSTRALIA =
      new RiskTerritoryConfiguration(
          ARTILLERY,
          AUSTRALIA,
          EASTERN_AUSTRALIA, INDONESIA, NEW_GUINEA);
  public static final Collection<RiskTerritoryConfiguration> allTerritories = Arrays
      .asList(ALASKA, ALBERTA, CENTRAL_AMERICA, EASTERN_UNITED_STATES, GREENLAND,
          NORTHWEST_TERRITORY, ONTARIO, QUEBEC, WESTERN_UNITED_STATES, ARGENTINA, BRAZIL, PERU,
          VENEZUELA, GREAT_BRITAIN, ICELAND, NORTHERN_EUROPE, SCANDINAVIA, SOUTHERN_EUROPE, UKRAINE,
          WESTERN_EUROPE, CENTRAL_AFRICA, EAST_AFRICA, EGYPT, MADAGASCAR, NORTH_AFRICA,
          SOUTH_AFRICA, AFGHANISTAN, CHINA, INDIA, IRKUTSK, JAPAN, KAMCHATKA, MIDDLE_EAST, MONGOLIA,
          SIAM, SIBERIA, URAL, YAKUTSK, EASTERN_AUSTRALIA, INDONESIA, NEW_GUINEA,
          WESTERN_AUSTRALIA);
  //NORTH_AMERICA
  private static int tid = 0;
  private int territoryId;
  private int cardType;
  private int continentId;
  private Set<Integer> connects;

  public RiskTerritoryConfiguration(int cardType, RiskContinentConfiguration continentConfiguration,
      RiskTerritoryConfiguration... connectsWith) {
    territoryId = tid++;
    continentId = continentConfiguration.getContinentId();
    this.cardType = cardType;
    this.connects = getConnectsSet(connectsWith);
  }

  public RiskTerritoryConfiguration() {
  }

  public RiskTerritoryConfiguration(int territoryId, int cardType, int continentId) {
    this(territoryId, cardType, continentId, Collections.emptySet());
  }

  public RiskTerritoryConfiguration(int territoryId, int cardType, int continentId,
      Set<Integer> connects) {
    this.territoryId = territoryId;
    this.cardType = cardType;
    this.continentId = continentId;
    this.connects = connects;
  }

  public static Set<Integer> getConnectsSet(RiskTerritoryConfiguration... configurations) {
    return Arrays.stream(configurations)
        .map(configuration -> configuration.territoryId)
        .collect(Collectors.toSet());
  }

  public RiskTerritory getTerritory() {
    return new RiskTerritory(continentId);
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

  public int getContinentId() {
    return continentId;
  }

  public void setContinentId(int continentId) {
    this.continentId = continentId;
  }
}
