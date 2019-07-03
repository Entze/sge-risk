package dev.entze.sge.game.risk.board;

import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import dev.entze.sge.game.risk.mission.RiskMission;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import dev.entze.sge.game.risk.util.PriestLogic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
  private final Set<RiskMission> allMissions;
  private final RiskMission[] playerMissions;

  private final RiskCard[][] playerCards;

  private final Map<Integer, RiskContinent> continents;

  private final int[] nonDeployedReinforcements;
  //private final Map<Integer, Integer> involvedTroopsInAttacks;
  private int attackingId;
  private int defendingId;
  private int troops;
  private RiskPhase phase;

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
      allMissions = configuration.getMissions().stream().map(RiskMissionConfiguration::getMission)
          .filter(m -> m.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER && m.getTargetIds()
              .stream().allMatch(i -> i < numberOfPlayers)).collect(Collectors.toUnmodifiableSet());
      playerMissions = new RiskMission[numberOfPlayers];
      selectRandomMissions(new ArrayList<>(allMissions), playerMissions);
    } else {
      allMissions = null;
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
    int[] initialTroops = configuration.getInitialTroops();
    Arrays.fill(nonDeployedReinforcements,
        initialTroops[Math.max(0, Math.min(numberOfPlayers - 2, initialTroops.length - 1))]);
    if (!configuration.isChooseInitialTerritories()) {
      List<Integer> ids = new ArrayList<>(territories.keySet());
      Collections.shuffle(ids);
      int p;
      {
        int i;
        for (p = numberOfPlayers - 1, i = 0;
            i < ids.size();
            i++, p = (p + (numberOfPlayers - 1)) % numberOfPlayers) {
          RiskTerritory territory = territories.get(ids.get(i));
          territory.setOccupantPlayerId(p);
          territory.setTroops(1);
          nonDeployedReinforcements[p]--;
        }
      }
    }

    attackingId = -1;
    defendingId = -1;
    troops = 0;

    phase = RiskPhase.REINFORCEMENT;

    map = configuration.getMap();
  }

  public RiskBoard(RiskBoard riskBoard) {
    this(riskBoard.numberOfPlayers, riskBoard.maxAttackerDice, riskBoard.maxDefenderDice,
        riskBoard.withCards, riskBoard.cardTypesWithoutJoker,
        riskBoard.reinforcementAtLeast, riskBoard.reinforcementThreshold,
        riskBoard.occupyOnlyWithAttackingArmies, riskBoard.fortifyOnlyFromSingleTerritory,
        riskBoard.fortifyOnlyWithNonFightingArmies, riskBoard.withMissions,
        riskBoard.gameBoard, riskBoard.territories, riskBoard.deckOfCards, riskBoard.allMissions,
        riskBoard.playerMissions,
        riskBoard.playerCards,
        riskBoard.continents, riskBoard.nonDeployedReinforcements, riskBoard.attackingId,
        riskBoard.defendingId, riskBoard.troops, riskBoard.phase, riskBoard.map);
  }

  public RiskBoard(int numberOfPlayers, int maxAttackerDice, int maxDefenderDice, boolean withCards,
      int cardTypesWithoutJoker, int reinforcementAtLeast,
      int reinforcementThreshold, boolean occupyOnlyWithAttackingArmies,
      boolean fortifyOnlyFromSingleTerritory, boolean fortifyOnlyWithNonFightingArmies,
      boolean withMissions,
      Graph<Integer, DefaultEdge> gameBoard, Map<Integer, RiskTerritory> territories,
      Collection<RiskCard> deckOfCards, Set<RiskMission> allMissions, RiskMission[] playerMissions,
      RiskCard[][] playerCards,
      Map<Integer, RiskContinent> continents, int[] nonDeployedReinforcements, int attackingId,
      int defendingId, int troops, RiskPhase phase,
      String map) {
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
    this.allMissions = allMissions;
    this.playerMissions = playerMissions != null ? playerMissions.clone() : null;
    this.playerCards = playerCards != null ? playerCards.clone() : null;
    this.continents = continents;
    this.nonDeployedReinforcements = nonDeployedReinforcements.clone();
    this.attackingId = attackingId;
    this.defendingId = defendingId;
    this.troops = troops;
    this.phase = phase;
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

  public boolean isTerritory(int territoryId) {
    return territories.containsKey(territoryId);
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

  public void initialSelect(int selected, int playerId) {
    RiskTerritory territory = territories.get(selected);
    territory.setOccupantPlayerId(playerId);
    territory.setTroops(1);
    nonDeployedReinforcements[playerId]--;
  }

  public void endMove(int nextPlayer) {
    phase = RiskPhase.REINFORCEMENT;
    awardReinforcements(nextPlayer);
  }

  private void awardReinforcements(int player) {
    int occupiedTerritories = Math.toIntExact(territories.values().stream()
        .filter(t -> t.getOccupantPlayerId() == player).count());
    int reinforcements = Math
        .max(reinforcementAtLeast, occupiedTerritories / reinforcementThreshold);

    for (Entry<Integer, RiskContinent> continent : continents.entrySet()) {
      if (continentConquered(player, continent.getKey())) {
        reinforcements += continent.getValue().getTroopBonus();
      }
    }

    nonDeployedReinforcements[player] += reinforcements;
  }

  public boolean areReinforcementsLeft() {
    for (int nonDeployedReinforcement : nonDeployedReinforcements) {
      if (nonDeployedReinforcement > 0) {
        return true;
      }
    }
    return false;
  }

  public int reinforcementsLeft(int player) {
    return nonDeployedReinforcements[player];
  }

  public boolean isReinforcementPhase() {
    return phase == RiskPhase.REINFORCEMENT;
  }

  public boolean isAttackPhase() {
    return phase == RiskPhase.ATTACK;
  }

  public boolean isOccupyPhase() {
    return phase == RiskPhase.OCCUPY;
  }

  public boolean isFortifyPhase() {
    return phase == RiskPhase.FORTIFY;
  }

  public int getMaxAttackerDice() {
    return maxAttackerDice;
  }

  public int getMaxDefenderDice() {
    return maxDefenderDice;
  }

  public int getNrOfDefenderDice() {

    if (!territories.containsKey(defendingId)) {
      return 0;
    }

    return Math.min(maxDefenderDice, territories.get(defendingId).getTroops());
  }

  public int getNrOfAttackerDice() {

    if (!territories.containsKey(attackingId)) {
      return 0;
    }

    return Math.min(maxAttackerDice, troops);
  }

  public void reinforce(int player, int reinforcedId, int troops) {
    if (territories.containsKey(reinforcedId)) {
      RiskTerritory territory = territories.get(reinforcedId);
      territory.setTroops(territory.getTroops() + troops);
      nonDeployedReinforcements[player] -= troops;
    }
  }

  public void endReinforcementPhase() {
    phase = RiskPhase.ATTACK;
  }

  public Set<Integer> neighboringTerritories(int territoryId) {
    return Graphs.neighborSetOf(gameBoard, territoryId);
  }

  public Set<Integer> neighboringEnemyTerritories(int territoryId) {
    final int self = getTerritoryOccupantId(territoryId);
    return Graphs.neighborSetOf(gameBoard, territoryId).stream()
        .filter(id -> getTerritoryOccupantId(id) != self).collect(Collectors.toSet());
  }

  public int getMaxAttackingTroops(int attackingId) {
    int troops = getTerritoryTroops(attackingId);
    if (occupyOnlyWithAttackingArmies) {
      return troops;
    }

    return Math.min(troops, getMaxAttackerDice());
  }

  public boolean areNeighbors(int territoryId1, int territoryId2) {
    return gameBoard.containsEdge(territoryId1, territoryId2);
  }

  public Set<Integer> occupiedTerritoriesByPlayer(final int playerId) {
    return territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId).map(Entry::getKey)
        .collect(Collectors.toSet());
  }

  public int nrOfTerritoriesOccupiedByPlayer(final int playerId) {
    return Math.toIntExact(territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId).count());
  }

  public boolean playerStillAlive(int playerId) {
    return nrOfTerritoriesOccupiedByPlayer(playerId) > 0;
  }

  public Set<Integer> occupiedTerritoriesByPlayerWithMoreThan1Troops(final int playerId) {
    return territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId
            && entry.getValue().getTroops() > 1).map(Entry::getKey)
        .collect(Collectors.toSet());
  }

  public void startAttack(int attackingId, int defendingId, int troops) {
    this.attackingId = attackingId;
    this.defendingId = defendingId;
    this.troops = troops;
  }

  public boolean isAttack() {
    return attackingId >= 0 && defendingId >= 0 && troops > 0;
  }

  public PriestLogic missionFulfilled(int player) {
    if (playerMissions == null) {
      return PriestLogic.FALSE;
    }
    RiskMission mission = playerMissions[player];

    if (mission.getRiskMissionType() == RiskMissionType.WILDCARD ||
        mission.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER) {
      return missionFulfilled(mission);
    } else if (mission.getRiskMissionType() == RiskMissionType.OCCUPY_TERRITORY) {
      return PriestLogic.fromBoolean(
          territoriesOccupied(player, mission.getTargetIds(), mission.getOccupyingWith()));
    } else if (mission.getRiskMissionType() == RiskMissionType.CONQUER_CONTINENT) {
      Set<Integer> conqueredContinents = playerConqueredContinents()
          .getOrDefault(player, Collections.emptySet());
      return PriestLogic.fromBoolean(conqueredContinents.size() >= mission.getTargetIds().size()
          && mission.getTargetIds().stream().filter(i -> i >= 0)
          .allMatch(conqueredContinents::contains));
    }

    return PriestLogic.FALSE;
  }

  public PriestLogic missionFulfilled(RiskMission mission) {
    if (mission.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER) {
      return PriestLogic
          .fromBoolean(mission.getTargetIds().stream().noneMatch(this::playerStillAlive));
    } else if (mission.getRiskMissionType() == RiskMissionType.CONQUER_CONTINENT) {
      return PriestLogic.fromBoolean(playerConqueredContinents().entrySet().stream()
          .anyMatch(
              e -> e.getValue().size() >= mission.getTargetIds().size()
                  //at least the required amount of continents are conquered
                  && mission.getTargetIds().stream().filter(id -> id >= 0)
                  .allMatch(
                      id -> e.getValue()
                          .contains(id)))); //all the required continents are conquered
    } else if (mission.getRiskMissionType() == RiskMissionType.OCCUPY_TERRITORY) {
      return PriestLogic.fromBoolean(IntStream.range(0, numberOfPlayers).anyMatch(
          p -> territoriesOccupied(p, mission.getTargetIds(), mission.getOccupyingWith())));
    } else if (mission.getRiskMissionType() == RiskMissionType.WILDCARD) {
      PriestLogic v = PriestLogic.FALSE;
      Iterator<RiskMission> iterator = allMissions.stream()
          .filter(m -> m.getRiskMissionType() != RiskMissionType.WILDCARD)
          .iterator(); //TODO: TEST, potential source of bugs
      if (iterator.hasNext()) {
        RiskMission aMission = iterator.next();
        v = missionFulfilled(aMission);
        while (iterator.hasNext() && PriestLogic.certain(v)) {
          aMission = iterator.next();
          v = PriestLogic.maybe(v, missionFulfilled(aMission));
        }
      }
      return v;
    }

    return PriestLogic.FALSE;
  }

  private boolean continentsConquered(int player, Collection<Integer> targetIds) {
    return targetIds.stream().allMatch(c -> continentConquered(player, c));
  }

  private boolean continentConquered(int player, int continent) {
    return continents.containsKey(continent) && territories.values().stream()
        .filter(t -> t.getContinentId() == continent)
        .allMatch(t -> t.getOccupantPlayerId() == player);
  }

  private boolean territoriesOccupied(int player, Collection<Integer> targetIds, int atLeast) {
    Set<Integer> occupiedTerritories = occupiedTerritoriesByPlayer(player);

    return occupiedTerritories.size() <= targetIds.size() // enough territories occupied
        && occupiedTerritories.stream().allMatch(
        t -> getTerritoryTroops(t) >= atLeast)
        // all territories occupied with at least required amount
        && targetIds.stream().filter(i -> i >= 0)
        .allMatch(occupiedTerritories::contains); // all required territories occupied
  }

  private Map<Integer, Set<Integer>> playerConqueredContinents() {
    Map<Integer, Map<Integer, RiskTerritory>> continents = new HashMap<>();
    for (Entry<Integer, RiskTerritory> territory : territories.entrySet()) {
      int continent = territory.getValue().getContinentId();
      continents.putIfAbsent(continent, new HashMap<>());
      continents.get(continent).put(territory.getKey(), territory.getValue());
    }

    Set<Integer> toRemove = new TreeSet<>();

    for (Entry<Integer, Map<Integer, RiskTerritory>> continent : continents.entrySet()) {
      if (continent.getValue().values().stream().mapToInt(RiskTerritory::getOccupantPlayerId)
          .distinct().count() != 1) {
        toRemove.add(continent.getKey());
      }
    }

    for (Integer remove : toRemove) {
      continents.remove(remove);
    }

    Map<Integer, Set<Integer>> playerConqueredContinents = new HashMap<>();

    for (Entry<Integer, Map<Integer, RiskTerritory>> continent : continents.entrySet()) {
      int player = continent.getValue().values().stream().findFirst()
          .map(RiskTerritory::getOccupantPlayerId).orElse(-1);

      if (player >= 0) {
        playerConqueredContinents.putIfAbsent(player, new TreeSet<>());
        playerConqueredContinents.get(player).add(continent.getKey());
      }

    }

    return playerConqueredContinents;
  }

  private enum RiskPhase {
    REINFORCEMENT,
    ATTACK,
    OCCUPY,
    FORTIFY,
  }

  private static void selectRandomMissions(List<RiskMission> missionList,
      RiskMission[] playerMissions) {
    Collections.shuffle(missionList);
    for (int i = missionList.size() - 1; i < playerMissions.length; i++) {
      missionList.add(RiskMission.FALLBACK);
    }
    for (int i = 0; i < playerMissions.length; i++) {
      playerMissions[i] = missionList.get(i);
      final int finalI = i;
      if (playerMissions[i].getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER
          && playerMissions[i].getTargetIds().stream().anyMatch(id -> id == finalI)) {
        playerMissions[i] = RiskMission.FALLBACK;
      }
    }
  }

}
