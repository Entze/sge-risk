package dev.entze.sge.game.risk.board;

import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import dev.entze.sge.game.risk.mission.RiskMission;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class RiskBoard {

  //settings
  private final int numberOfPlayers;
  private final int maxAttackerDice;
  private final int maxDefenderDice;

  private final boolean withCards;
  private final int cardTypesWithoutJoker;

  private final int reinforcementAtLeast;
  private final int reinforcementThreshold;
  private final boolean occupyOnlyWithAttackingArmies;
  private final boolean fortifyOnlyFromSingleTerritory;
  private final boolean fortifyOnlyWithNonFightingArmies;
  private final boolean withMissions;

  //board
  private final Graph<Integer, DefaultEdge> gameBoard;
  private final Map<Integer, RiskTerritory> territories;
  private final Deque<RiskCard> deckOfCards;
  private final RiskMission[] playerMissions;

  private final RiskCard[][] playerCards;

  private final Map<Integer, RiskContinent> continents;

  private final int[] nonDeployedReinforcements;

  private final String map;

  public RiskBoard(RiskConfiguration configuration, int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
    maxAttackerDice = configuration.getMaxAttackerDice();
    maxDefenderDice = configuration.getMaxDefenderDice();
    withCards = configuration.isWithCards();
    cardTypesWithoutJoker = configuration.getCardTypesWithoutJoker();
    reinforcementAtLeast = configuration.getReinforcementAtLeast();
    reinforcementThreshold = configuration.getReinforcementThreshold();
    occupyOnlyWithAttackingArmies = configuration.isOccupyOnlyWithAttackingArmies();
    fortifyOnlyFromSingleTerritory = configuration.isFortifyOnlyFromSingleTerritory();
    fortifyOnlyWithNonFightingArmies = configuration.isFortifyOnlyWithNonFightingArmies();
    withMissions = configuration.isWithMissions();
    if (withMissions && !configuration.getMissions().isEmpty()) {
      List<RiskMission> missionList = new ArrayList<>();
      for (RiskMissionConfiguration mission : configuration.getMissions()) {
        missionList.add(mission.getMission());
      }
      missionList.removeIf(
          mission -> mission.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER && mission
              .getTargetIds().stream().anyMatch(id -> id >= numberOfPlayers));
      Collections.shuffle(missionList);
      playerMissions = new RiskMission[numberOfPlayers];
      for (int i = 0; i < playerMissions.length; i++) {
        playerMissions[i] = missionList.get(i);
        final int finalI = i;
        if (playerMissions[i].getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER
            && playerMissions[i].getTargetIds().stream().anyMatch(id -> id == finalI)) {
          playerMissions[i] = RiskMission.FALLBACK;
        }
      }
    } else {
      playerMissions = null;
    }
    Set<RiskTerritoryConfiguration> territoriesConfiguration = configuration.getTerritories();

    territories = territoriesConfiguration.stream().collect(
        Collectors.toUnmodifiableMap(RiskTerritoryConfiguration::getTerritoryId,
            RiskTerritoryConfiguration::getTerritory, (a, b) -> b));

    gameBoard = new SimpleGraph<>(DefaultEdge.class);
    for (RiskTerritoryConfiguration territoryConfiguration : territoriesConfiguration) {
      gameBoard.addVertex(territoryConfiguration.getTerritoryId());
    }
    for (RiskTerritoryConfiguration territoryConfiguration : territoriesConfiguration) {
      for (Integer connect : territoryConfiguration.getConnects()) {
        gameBoard.addEdge(territoryConfiguration.getTerritoryId(), connect);
      }
    }

    if (withCards) {
      List<RiskCard> cardList = territoriesConfiguration.stream().map(
          territoryConfiguration -> new RiskCard(territoryConfiguration.getCardType(),
              territoryConfiguration.getTerritoryId()))
          .collect(Collectors.toCollection(() -> new ArrayList<>(
              territoriesConfiguration.size() + configuration.getNumberOfJokers())));
      for (int i = 0; i < configuration.getNumberOfJokers(); i++) {
        cardList.add(new RiskCard(RiskCard.JOKER, -1));
      }
      Collections.shuffle(cardList);
      deckOfCards = new ArrayDeque<>(cardList);
      playerCards = new RiskCard[numberOfPlayers][cardTypesWithoutJoker * 3];
    } else {
      deckOfCards = null;
      playerCards = null;
    }

    Set<RiskContinentConfiguration> continentsConfiguration = configuration.getContinents();

    this.continents = continentsConfiguration.stream().collect(Collectors
        .toUnmodifiableMap(RiskContinentConfiguration::getContinentId,
            RiskContinentConfiguration::getContinent, (a, b) -> b));

    nonDeployedReinforcements = new int[numberOfPlayers];
    Arrays.fill(nonDeployedReinforcements, configuration.getInitialTroops()[numberOfPlayers - 2]);
    if (!configuration.isChooseInitialTerritories()) {
      List<Integer> ids = new ArrayList<>(territories.keySet());
      Collections.shuffle(ids);
      for (int p = 0; p < ids.size(); p++) {
        RiskTerritory territory = territories.get(ids.get(p));
        territory.setOccupantPlayerId(p % numberOfPlayers);
        territory.setTroops(1);
        nonDeployedReinforcements[p % numberOfPlayers]--;
      }
    }

    map = configuration.getMap();
  }

  public RiskBoard(RiskBoard riskBoard) {
    this(riskBoard.numberOfPlayers, riskBoard.maxAttackerDice, riskBoard.maxDefenderDice,
        riskBoard.withCards, riskBoard.cardTypesWithoutJoker,
        riskBoard.reinforcementAtLeast, riskBoard.reinforcementThreshold,
        riskBoard.occupyOnlyWithAttackingArmies, riskBoard.fortifyOnlyFromSingleTerritory,
        riskBoard.fortifyOnlyWithNonFightingArmies, riskBoard.withMissions,
        riskBoard.gameBoard, riskBoard.territories, riskBoard.deckOfCards, riskBoard.playerMissions,
        riskBoard.playerCards,
        riskBoard.continents, riskBoard.nonDeployedReinforcements, riskBoard.map);
  }

  public RiskBoard(int numberOfPlayers, int maxAttackerDice, int maxDefenderDice, boolean withCards,
      int cardTypesWithoutJoker, int reinforcementAtLeast,
      int reinforcementThreshold, boolean occupyOnlyWithAttackingArmies,
      boolean fortifyOnlyFromSingleTerritory, boolean fortifyOnlyWithNonFightingArmies,
      boolean withMissions,
      Graph<Integer, DefaultEdge> gameBoard, Map<Integer, RiskTerritory> territories,
      Collection<RiskCard> deckOfCards, RiskMission[] playerMissions, RiskCard[][] playerCards,
      Map<Integer, RiskContinent> continents, int[] nonDeployedReinforcements, String map) {
    this.numberOfPlayers = numberOfPlayers;
    this.maxAttackerDice = maxAttackerDice;
    this.maxDefenderDice = maxDefenderDice;
    this.withCards = withCards;
    this.cardTypesWithoutJoker = cardTypesWithoutJoker;
    this.reinforcementAtLeast = reinforcementAtLeast;
    this.reinforcementThreshold = reinforcementThreshold;
    this.occupyOnlyWithAttackingArmies = occupyOnlyWithAttackingArmies;
    this.fortifyOnlyFromSingleTerritory = fortifyOnlyFromSingleTerritory;
    this.fortifyOnlyWithNonFightingArmies = fortifyOnlyWithNonFightingArmies;
    this.withMissions = withMissions;
    this.gameBoard = gameBoard;
    this.territories = Collections.unmodifiableMap(copyTerritories(territories));
    this.deckOfCards = deckOfCards != null ? new ArrayDeque<>(deckOfCards) : null;
    this.playerMissions = playerMissions != null ? playerMissions.clone() : null;
    this.playerCards = playerCards != null ? playerCards.clone() : null;
    this.continents = continents;
    this.nonDeployedReinforcements = nonDeployedReinforcements.clone();
    this.map = map;
  }

  public int getNumberOfPlayers() {
    return numberOfPlayers;
  }

  public Map<Integer, RiskTerritory> getTerritories() {
    return territories;
  }

  public Set<Integer> getTerritoryIds() {
    return territories.keySet();
  }

  public int getTerritoryOccupantId(int territoryId) {
    return territories.containsKey(territoryId) ? territories.get(territoryId).getOccupantPlayerId()
        : -1;
  }

  public int getTerritoryTroops(int territoryId) {
    return territories.containsKey(territoryId) ? territories.get(territoryId).getTroops() : 0;
  }

  public String getMap() {
    return map;
  }

  private static Map<Integer, RiskTerritory> copyTerritories(
      Map<Integer, RiskTerritory> territories) {
    return territories.keySet().stream().collect(Collectors
        .toMap(i -> i, i -> new RiskTerritory(territories.get(i)), (a, b) -> b));
  }

}
