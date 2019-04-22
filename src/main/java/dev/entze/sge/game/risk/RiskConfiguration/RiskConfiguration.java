package dev.entze.sge.game.risk.RiskConfiguration;

import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.AFRICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.ASIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.AUSTRALIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.EUROPE;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.NORTH_AMERICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.SOUTH_AMERICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskContinentConfiguration.WILDCARD;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskMissionConfiguration.continentGroup;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.AFGHANISTAN;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.ALASKA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.ALBERTA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.ARGENTINA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.BRAZIL;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.CENTRAL_AFRICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.CENTRAL_AMERICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.CHINA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.EASTERN_AUSTRALIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.EASTERN_UNITED_STATES;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.EAST_AFRICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.EGYPT;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.GREAT_BRITAIN;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.GREENLAND;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.ICELAND;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.INDIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.INDONESIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.IRKUTSK;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.JAPAN;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.KAMCHATKA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.MADAGASCAR;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.MIDDLE_EAST;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.MONGOLIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.NEW_GUINEA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.NORTHERN_EUROPE;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.NORTHWEST_TERRITORY;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.NORTH_AFRICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.ONTARIO;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.PERU;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.QUEBEC;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.SCANDINAVIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.SIAM;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.SIBERIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.SOUTHERN_EUROPE;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.SOUTH_AFRICA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.UKRAINE;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.URAL;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.VENEZUELA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.WESTERN_AUSTRALIA;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.WESTERN_EUROPE;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.WESTERN_UNITED_STATES;
import static dev.entze.sge.game.risk.RiskConfiguration.RiskTerritoryConfiguration.YAKUTSK;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class RiskConfiguration {

  public static final RiskConfiguration RISK_DEFAULT_CONFIG = new RiskConfiguration(6,
      3, 2,
      new int[] {50, 35, 30, 25, 20},
      true, 3, 2,
      true,
      3, 3,
      false, true, false, true,
      RiskMissionConfiguration.defaultMissions(0, 6,
          Arrays.asList(
              continentGroup(ASIA, SOUTH_AMERICA),
              continentGroup(ASIA, AFRICA),
              continentGroup(NORTH_AMERICA, AFRICA),
              continentGroup(NORTH_AMERICA, AUSTRALIA),
              continentGroup(EUROPE, SOUTH_AMERICA, WILDCARD),
              continentGroup(EUROPE, AUSTRALIA, WILDCARD)),
          new int[] {18, 24}, new int[] {2, 1}),
      RiskContinentConfiguration.allContinents,
      RiskTerritoryConfiguration.allTerritories, ""
      + "zzj--------zzzn\n"
      + "zz6+-----------/8\\zzzm\n"
      + "zz5/l/zs+----+zn\n"
      + "zc+--------+-----------------+-8[" + GREENLAND.getTerritoryId()
      + "]8+--+k+---+w+-----+6\\1/-----------------------------+p\n"
      + "f+-------+m/a\\f/2\\f/n/5\\m+-------+e+v\\o\n"
      + "a+---/8/++-------------------+c\\3+---------+4|a---\\k+--+7+-+------------------+9\\8["
      + SIBERIA.getTerritoryId() + "]5\\c[" + YAKUTSK.getTerritoryId() + "]i+-------------+9\n"
      + "9/c/z1\\1/8+------+4+----/1+-------+-------\\5/d|j\\9+----+9+t/f+8\n"
      + "3+----+4[" + ALASKA.getTerritoryId() + "]7/f[" + NORTHWEST_TERRITORY.getTerritoryId()
      + "]d+-------++8/7|3/7|3[" + ICELAND.getTerritoryId() + "]3|8+--1+2[" + SCANDINAVIA
      .getTerritoryId() + "]3+7+j+f\\9\\4+j+--+h\\7\n"
      + "2/h/s/8/2\\6/8\\1/9+-------+7/3/6/1\\5/j/8[" + URAL.getTerritoryId()
      + "]8\\_8+2/1\\h/6+5+4[" + KAMCHATKA.getTerritoryId() + "]4+6\n"
      + "++6+-----------+--------------------+------+8|5\\---+a+j\\5/4+-----+3+---+j+k\\6/1\\/3+--------+2+---+6/1\\3/1\\8|6\n"
      + "\\5++c|j/g|a\\q+---+---+7\\a/l\\k\\4/8[" + IRKUTSK.getTerritoryId()
      + "]8\\/a+3+-+3+7+------\n"
      + "1\\---+e|8[" + ALBERTA.getTerritoryId() + "]9/8[" + ONTARIO.getTerritoryId() + "]8|4["
      + QUEBEC.getTerritoryId() + "]6\\p|3[" + GREAT_BRITAIN.getTerritoryId() + "]3|8+--------+b["
      + UKRAINE.getTerritoryId() + "]b+------------+7\\2/j+-----+4|a\\5/7\n"
      + "k/h/h/c|p+---+---+------1/4[" + NORTHERN_EUROPE.getTerritoryId()
      + "]5\\l/e\\6/1/5/---+a|6\\3+-+9+---+8\n"
      + "j/-----------------+---------9|8+---+u\\9+c\\j+g+----+-+-----+5\\2/------+7\\1/3\\l\n"
      + "i/j/8\\8/7/z1+-------+---+----------+--+e/8[" + AFGHANISTAN.getTerritoryId()
      + "]c/3\\b++g+5\\k\n"
      + "h/j/a+------+-------+z1/4[" + WESTERN_EUROPE.getTerritoryId() + "]5/7[" + SOUTHERN_EUROPE
      .getTerritoryId() + "]8\\c+l/5\\s|6+---+f\n"
      + "g/9[" + WESTERN_UNITED_STATES.getTerritoryId() + "]9|i___/z5/a/\\h+---+7|k/7+----+5["
      + MONGOLIA.getTerritoryId() + "]7+----+3+-----/5\\e\n"
      + "f/k|h/z8+----+-----+2\\1/+------+-+3/5\\------+e+----+e\\b/6\\1/5+3[" + JAPAN
      .getTerritoryId() + "]3+d\n"
      + "f|d_______|6[" + EASTERN_UNITED_STATES.getTerritoryId()
      + "]9/ze|9+b\\1/e\\c/6\\e+---------+8+6|6/e\n"
      + "f|c|n/zc+--+---+4/|c+-----+a+------+---+8+--+t\\6+-+3+f\n"
      + "f\\c|h_____/zc/8\\2/1|h/j\\f\\g[" + CHINA.getTerritoryId() + "]c+8\\1/g\n"
      + "g\\------------g/zh/a+/2+----------------+l\\f+--+5+k\\8+h\n"
      + "h\\b|4___________|zg/c\\1/i\\l\\9[" + INDIA.getTerritoryId() + "]8\\3/1\\k+p\n"
      + "i\\a----/zp+-+e+9[" + EGYPT.getTerritoryId() + "]a\\8[" + MIDDLE_EAST.getTerritoryId()
      + "]c+---+e+-+3\\j|p\n"
      + "j\\b|zq/i\\k\\j/5\\e/5+---+3+--+-------+p\n"
      + "k\\5[" + CENTRAL_AMERICA.getTerritoryId()
      + "]4|zp/k+-------+------------+a+--+3/7+-+a+---+7\\1/3|x\n"
      + "l\\a\\--\\zk+u\\c\\a\\2\\1/a|9/5+7+4+x\n"
      + "m+--+b\\zi|d[" + NORTH_AFRICA.getTerritoryId() + "]h+c\\a\\2+b+5+--+7\\c\\w\n"
      + "q+-----+6\\za+-----+v|d\\5+----+e\\5\\a+-+3[" + SIAM.getTerritoryId() + "]6+v\n"
      + "x\\__4\\8+--+v/6|v+e\\5\\j+5+c\\8/w\n"
      + "z1\\4+------+4+-----+n/7+--+r/9[" + EAST_AFRICA.getTerritoryId()
      + "]6+-----+j\\4|d+3+--+x\n"
      + "z2`-+-\\i\\l/c\\7+-----+7+----+l/l+3+e\\1/z2\n"
      + "z7\\7[" + VENEZUELA.getTerritoryId() + "]a+-+h/e\\5/7\\5/7\\i/n\\1/g+z3\n"
      + "z8\\b+-+6\\f/g+---+9\\3/9+-------+8/p+i\\z2\n"
      + "z8/8+-+3+------+d/w\\1/h/8/zb\\z1\n"
      + "z7+----+3/d+-----+6/y+8[" + CENTRAL_AFRICA.getTerritoryId()
      + "]8/8/zb+-------+---------------+-------+5\n"
      + "z6/4`1\\1/k+-----+z1\\f/8+zc|3[" + INDONESIA.getTerritoryId() + "]3|f|3[" + NEW_GUINEA
      .getTerritoryId() + "]3|5\n"
      + "z6|7+s\\z1+---+9+8/1\\zb+-------+f+-------+5\n"
      + "z7\\6|t+z|4\\9\\6/3\\zj\\c/1|d\n"
      + "z8\\5+--+c[" + BRAZIL.getTerritoryId() + "]d|z+5\\9\\4/5\\zj\\a/2|d\n"
      + "z9\\8+--+j+--+y/7+---------+--+7+-----+zd\\8/3|d\n"
      + "za\\4[" + PERU.getTerritoryId() + "]6\\i|z2|k+------+5/zf\\3/----+--+--+3+-+4\n"
      + "zb\\b\\h|z2\\j/6/5+zh+-+4/7\\1/3\\3\n"
      + "zc+--+8\\g+z3|h+6/2[" + MADAGASCAR.getTerritoryId() + "]3|zg/6/9+5+2\n"
      + "zg\\8\\e/z4\\7[" + SOUTH_AFRICA.getTerritoryId() + "]9|5|7+zb+---+6/h\\1\n"
      + "zh+--------+9+--+z6\\g+5|6/zb/a/c[" + EASTERN_AUSTRALIA.getTerritoryId() + "]6+\n"
      + "zh|9\\7/zb+e/6+-----+zb+a+---------+9/1\n"
      + "zh|a\\5/zd\\9+--+zp|5[" + WESTERN_AUSTRALIA.getTerritoryId() + "]d/9+2\n"
      + "zh|b+3/zf+7/zt|i/a|2\n"
      + "zh|5[" + ARGENTINA.getTerritoryId() + "]6\\1/zg|6/zu+-------------+.2/9+-+2\n"
      + "zh|d+zh+-----+zzb\\1/9/5\n"
      + "zh|c/zzzz2+6+--+6\n"
      + "zh|9+-+zzzz4\\4/a\n"
      + "zh+8/zzzz8+--+b\n"
      + "zi\\6/zzzzo\n"
      + "zj\\4+zzzzp\n"
      + "zk\\4\\zzzzo\n"
      + "zl\\4\\zzzzn\n"
      + "zm+-+2+zzzzm\n"
      + "zp\\1/zzzzm\n"
      + "zq+zzzzn"

  );


  private int maxNumberOfPlayers = 2;
  private int maxAttackerDice = 3;
  private int maxDefenderDice = 2;
  private int[] initialTroops = null;
  private boolean withCards = true;
  private int cardTypesWithoutJoker = 3;
  private int numberOfJokers = 2;
  private boolean chooseInitialTerritories = true;
  private int reinforcementAtLeast = 3;
  private int reinforcementThreshold = 3;
  private boolean occupyOnlyWithAttackingArmies = false;
  private boolean fortifyOnlyFromSingleTerritory = true;
  private boolean fortifyOnlyWithNonFightingArmies = false;
  private boolean withMissions = true;
  private Set<RiskMissionConfiguration> missions = new HashSet<>();
  private Set<RiskContinentConfiguration> continents;
  private Set<RiskTerritoryConfiguration> territories;
  private String map;

  public RiskConfiguration(int maxNumberOfPlayers, int maxAttackerDice, int maxDefenderDice,
      int[] initialTroops, boolean withCards, int cardTypesWithoutJoker, int numberOfJokers,
      boolean chooseInitialTerritories, int reinforcementAtLeast, int reinforcementThreshold,
      boolean occupyOnlyWithAttackingArmies, boolean fortifyOnlyFromSingleTerritory,
      boolean fortifyOnlyWithNonFightingArmies, boolean withMissions,
      Collection<RiskMissionConfiguration> missions,
      Collection<RiskContinentConfiguration> continents,
      Collection<RiskTerritoryConfiguration> territories, String map) {
    this.maxNumberOfPlayers = maxNumberOfPlayers;
    this.maxAttackerDice = maxAttackerDice;
    this.maxDefenderDice = maxDefenderDice;
    this.initialTroops = initialTroops;
    this.withCards = withCards;
    this.cardTypesWithoutJoker = cardTypesWithoutJoker;
    this.numberOfJokers = numberOfJokers;
    this.chooseInitialTerritories = chooseInitialTerritories;
    this.reinforcementAtLeast = reinforcementAtLeast;
    this.reinforcementThreshold = reinforcementThreshold;
    this.occupyOnlyWithAttackingArmies = occupyOnlyWithAttackingArmies;
    this.fortifyOnlyFromSingleTerritory = fortifyOnlyFromSingleTerritory;
    this.fortifyOnlyWithNonFightingArmies = fortifyOnlyWithNonFightingArmies;
    this.withMissions = withMissions;
    this.missions.addAll(missions);
    this.continents = new HashSet<>(continents);
    this.territories = new HashSet<>(territories);
    this.map = map;
  }


  public RiskConfiguration(
      Collection<RiskContinentConfiguration> continents,
      Collection<RiskTerritoryConfiguration> territories, String map) {
    this.continents = new HashSet<>(continents);
    this.territories = new HashSet<>(territories);
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

  public int getMaxNumberOfPlayers() {
    return maxNumberOfPlayers;
  }

  public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
    this.maxNumberOfPlayers = maxNumberOfPlayers;
  }

  public int getMaxAttackerDice() {
    return maxAttackerDice;
  }

  public void setMaxAttackerDice(int maxAttackerDice) {
    this.maxAttackerDice = maxAttackerDice;
  }

  public int getMaxDefenderDice() {
    return maxDefenderDice;
  }

  public void setMaxDefenderDice(int maxDefenderDice) {
    this.maxDefenderDice = maxDefenderDice;
  }

  public boolean isWithCards() {
    return withCards;
  }

  public void setWithCards(boolean withCards) {
    this.withCards = withCards;
  }

  public int getCardTypesWithoutJoker() {
    return cardTypesWithoutJoker;
  }

  public void setCardTypesWithoutJoker(int cardTypesWithoutJoker) {
    this.cardTypesWithoutJoker = cardTypesWithoutJoker;
  }

  public int getNumberOfJokers() {
    return numberOfJokers;
  }

  public void setNumberOfJokers(int numberOfJokers) {
    this.numberOfJokers = numberOfJokers;
  }

  public boolean isChooseInitialTerritories() {
    return chooseInitialTerritories;
  }

  public void setChooseInitialTerritories(boolean chooseInitialTerritories) {
    this.chooseInitialTerritories = chooseInitialTerritories;
  }

  public int getReinforcementAtLeast() {
    return reinforcementAtLeast;
  }

  public void setReinforcementAtLeast(int reinforcementAtLeast) {
    this.reinforcementAtLeast = reinforcementAtLeast;
  }

  public int getReinforcementThreshold() {
    return reinforcementThreshold;
  }

  public void setReinforcementThreshold(int reinforcementThreshold) {
    this.reinforcementThreshold = reinforcementThreshold;
  }

  public boolean isOccupyOnlyWithAttackingArmies() {
    return occupyOnlyWithAttackingArmies;
  }

  public void setOccupyOnlyWithAttackingArmies(boolean occupyOnlyWithAttackingArmies) {
    this.occupyOnlyWithAttackingArmies = occupyOnlyWithAttackingArmies;
  }

  public boolean isFortifyOnlyFromSingleTerritory() {
    return fortifyOnlyFromSingleTerritory;
  }

  public void setFortifyOnlyFromSingleTerritory(boolean fortifyOnlyFromSingleTerritory) {
    this.fortifyOnlyFromSingleTerritory = fortifyOnlyFromSingleTerritory;
  }

  public boolean isFortifyOnlyWithNonFightingArmies() {
    return fortifyOnlyWithNonFightingArmies;
  }

  public void setFortifyOnlyWithNonFightingArmies(boolean fortifyOnlyWithNonFightingArmies) {
    this.fortifyOnlyWithNonFightingArmies = fortifyOnlyWithNonFightingArmies;
  }

  public boolean isWithMissions() {
    return withMissions;
  }

  public void setWithMissions(boolean withMissions) {
    this.withMissions = withMissions;
  }

  public Set<RiskMissionConfiguration> getMissions() {
    return missions;
  }

  public void setMissions(
      Set<RiskMissionConfiguration> missions) {
    this.missions = missions;
  }

  public Set<RiskContinentConfiguration> getContinents() {
    return continents;
  }

  public void setContinents(
      Set<RiskContinentConfiguration> continents) {
    this.continents = continents;
  }

  public Set<RiskTerritoryConfiguration> getTerritories() {
    return territories;
  }

  public void setTerritories(
      Set<RiskTerritoryConfiguration> territories) {
    this.territories = territories;
  }

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }
}
