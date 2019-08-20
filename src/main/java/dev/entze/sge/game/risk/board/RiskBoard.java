package dev.entze.sge.game.risk.board;

import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import dev.entze.sge.game.risk.mission.RiskMission;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import dev.entze.sge.game.risk.util.PriestLogic;
import dev.entze.sge.util.Util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class RiskBoard {

  //settings
  private final int numberOfPlayers;
  private final int maxAttackerDice;
  private final int maxDefenderDice;

  private final boolean withCards;
  private final int[] tradeInBonus;
  private final int maxExtraBonus;
  private int tradeIns;
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
  private final Map<Integer, Graph<Integer, DefaultEdge>> fortifyConnectivityGraph;
  private final Map<Integer, ConnectivityInspector<Integer, DefaultEdge>> fortifyConnectivityInspector;


  private final Deque<RiskCard> deckOfCards;
  private final Set<RiskMission> allMissions;
  private final RiskMission[] playerMissions;

  private final Map<Integer, List<RiskCard>> playerCards;

  private final Map<Integer, RiskContinent> continents;

  private final int[] nonDeployedReinforcements;
  private final Set<Integer> reinforcedTerritories;
  private final Map<Integer, Integer> involvedTroopsInAttacks;
  private final String map;
  private int attackingId;
  private int defendingId;
  private int troops;
  private boolean hasOccupiedCountry;
  private RiskPhase phase;
  private boolean initialSelectMaybe;
  private boolean initialReinforceMaybe;

  RiskBoard(RiskConfiguration configuration, int numberOfPlayers) {
    this.numberOfPlayers = numberOfPlayers;
    maxAttackerDice = configuration.getMaxAttackerDice();
    maxDefenderDice = configuration.getMaxDefenderDice();
    withCards = configuration.isWithCards();
    cardTypesWithoutJoker = configuration.getCardTypesWithoutJoker();
    if (!(0 <= configuration.getNumberOfJokers()
        && configuration.getNumberOfJokers() < cardTypesWithoutJoker)) {
      throw new IllegalArgumentException(
          configuration.getNumberOfJokers() + " is an illegal number of jokers");
    }
    reinforcementAtLeast = configuration.getReinforcementAtLeast();
    reinforcementThreshold = configuration.getReinforcementThreshold();
    occupyOnlyWithAttackingArmies = configuration.isOccupyOnlyWithAttackingArmies();
    fortifyOnlyFromSingleTerritory = configuration.isFortifyOnlyFromSingleTerritory();
    fortifyOnlyWithNonFightingArmies = configuration.isFortifyOnlyWithNonFightingArmies();
    withMissions = configuration.isWithMissions();
    if (withMissions && !configuration.getMissions().isEmpty()) {
      allMissions = configuration.getMissions().stream()
          .map(RiskMissionConfiguration::getMission)
          .filter(m -> m.getRiskMissionType() != RiskMissionType.LIBERATE_PLAYER || m.getTargetIds()
              .stream().allMatch(i -> i < numberOfPlayers)).collect(Collectors.toUnmodifiableSet());
      playerMissions = new RiskMission[numberOfPlayers];
      selectRandomMissions(new ArrayList<>(allMissions), playerMissions);
    } else {
      allMissions = null;
      playerMissions = null;
    }
    Set<RiskTerritoryConfiguration> territoriesConfiguration = new HashSet<>(
        configuration.getTerritories());

    Map<Integer, RiskTerritory> territoryMap = new HashMap<>();

    for (RiskTerritoryConfiguration riskTerritoryConfiguration : territoriesConfiguration) {
      territoryMap.put(riskTerritoryConfiguration.getTerritoryId(),
          riskTerritoryConfiguration.getTerritory());
    }

    territories = Map.copyOf(territoryMap);

    gameBoard = new SimpleGraph<>(DefaultEdge.class);
    if (fortifyOnlyFromSingleTerritory) {
      fortifyConnectivityGraph = null;
    } else {
      fortifyConnectivityGraph = new HashMap<>();
      for (int p = 0; p < numberOfPlayers; p++) {
        fortifyConnectivityGraph.put(p, new SimpleGraph<>(DefaultEdge.class));
      }
    }
    for (RiskTerritoryConfiguration territoryConfiguration : territoriesConfiguration) {
      gameBoard.addVertex(territoryConfiguration.getTerritoryId());
      if (!fortifyOnlyFromSingleTerritory) {
        for (int p = 0; p < numberOfPlayers; p++) {
          fortifyConnectivityGraph.get(p).addVertex(territoryConfiguration.getTerritoryId());
        }
      }
    }
    for (RiskTerritoryConfiguration territoryConfiguration : territoriesConfiguration) {
      for (Integer connect : territoryConfiguration.getConnects()) {
        gameBoard.addEdge(territoryConfiguration.getTerritoryId(), connect);
      }
    }

    tradeIns = 0;
    if (withCards) {
      tradeInBonus = configuration.getTradeInBonus();
      maxExtraBonus = configuration.getMaxExtraBonus();

      List<RiskCard> cardList = territoriesConfiguration.stream().map(
          territoryConfiguration -> new RiskCard(territoryConfiguration.getCardType(),
              territoryConfiguration.getTerritoryId()))
          .collect(Collectors.toCollection(() -> new ArrayList<>(
              territoriesConfiguration.size() + configuration.getNumberOfJokers())));

      for (RiskCard riskCard : cardList) {
        if (!(1 <= riskCard.getCardType()
            && riskCard.getCardType() <= configuration.getCardTypesWithoutJoker())) {
          throw new IllegalArgumentException("Illegal card type found: " + riskCard.toString());
        }
      }

      for (int i = 0; i < configuration.getNumberOfJokers(); i++) {
        cardList.add(new RiskCard(RiskCard.JOKER, -1));
      }
      Collections.shuffle(cardList);
      deckOfCards = new ArrayDeque<>(cardList);
      playerCards = IntStream.range(0, numberOfPlayers).boxed().collect(Collectors
          .toUnmodifiableMap(p -> p, p -> new ArrayList<>(cardSlots())));

    } else {
      tradeInBonus = null;
      maxExtraBonus = 0;
      deckOfCards = null;
      playerCards = null;
    }

    Set<RiskContinentConfiguration> continentsConfiguration = new HashSet<>(
        configuration.getContinents());

    this.continents = continentsConfiguration.stream().collect(Collectors
        .toUnmodifiableMap(RiskContinentConfiguration::getContinentId,
            RiskContinentConfiguration::getContinent, (a, b) -> b));

    nonDeployedReinforcements = new int[numberOfPlayers];
    int[] initialTroops = configuration.getInitialTroops();
    Arrays.fill(nonDeployedReinforcements,
        initialTroops[Math.max(0, Math.min(numberOfPlayers - 2, initialTroops.length - 1))]);

    reinforcedTerritories = Collections.emptySet();

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

      if (!fortifyOnlyFromSingleTerritory) {
        for (DefaultEdge edge : gameBoard.edgeSet()) {
          int src = gameBoard.getEdgeSource(edge);
          int dst = gameBoard.getEdgeTarget(edge);
          int occupant;
          if ((occupant = getTerritoryOccupantId(src)) == getTerritoryOccupantId(dst)) {
            fortifyConnectivityGraph.get(occupant).addEdge(src, dst);
          }
        }
      }
    }

    if (fortifyOnlyFromSingleTerritory) {
      fortifyConnectivityInspector = null;
    } else {
      fortifyConnectivityInspector = new HashMap<>();

      for (int p = 0; p < numberOfPlayers; p++) {
        fortifyConnectivityInspector
            .put(p, new ConnectivityInspector<>(fortifyConnectivityGraph.get(p)));
      }
    }

    involvedTroopsInAttacks = new HashMap<>();

    attackingId = -1;
    defendingId = -1;
    troops = 0;
    hasOccupiedCountry = false;

    phase = RiskPhase.REINFORCEMENT;

    initialSelectMaybe = true;
    initialReinforceMaybe = true;

    map = configuration.getMap();
  }

  RiskBoard(RiskBoard riskBoard) {
    this(riskBoard.numberOfPlayers, riskBoard.maxAttackerDice, riskBoard.maxDefenderDice,
        riskBoard.withCards, riskBoard.tradeInBonus, riskBoard.maxExtraBonus, riskBoard.tradeIns,
        riskBoard.cardTypesWithoutJoker,
        riskBoard.reinforcementAtLeast, riskBoard.reinforcementThreshold,
        riskBoard.occupyOnlyWithAttackingArmies, riskBoard.fortifyOnlyFromSingleTerritory,
        riskBoard.fortifyOnlyWithNonFightingArmies, riskBoard.withMissions,
        riskBoard.gameBoard, riskBoard.territories, riskBoard.fortifyConnectivityGraph,
        riskBoard.fortifyConnectivityInspector, riskBoard.deckOfCards, riskBoard.allMissions,
        riskBoard.playerMissions,
        riskBoard.playerCards,
        riskBoard.continents, riskBoard.nonDeployedReinforcements, riskBoard.reinforcedTerritories,
        riskBoard.involvedTroopsInAttacks, riskBoard.attackingId,
        riskBoard.defendingId, riskBoard.troops, riskBoard.hasOccupiedCountry, riskBoard.phase,
        riskBoard.initialSelectMaybe, riskBoard.initialReinforceMaybe,
        riskBoard.map);
  }

  private RiskBoard(int numberOfPlayers, int maxAttackerDice, int maxDefenderDice,
      boolean withCards, int[] tradeInBonus, int maxExtraBonus, int tradeIns,
      int cardTypesWithoutJoker, int reinforcementAtLeast,
      int reinforcementThreshold, boolean occupyOnlyWithAttackingArmies,
      boolean fortifyOnlyFromSingleTerritory, boolean fortifyOnlyWithNonFightingArmies,
      boolean withMissions,
      Graph<Integer, DefaultEdge> gameBoard, Map<Integer, RiskTerritory> territories,
      Map<Integer, Graph<Integer, DefaultEdge>> fortifyConnectivityGraph,
      Map<Integer, ConnectivityInspector<Integer, DefaultEdge>> fortifyConnectivityInspector,
      Collection<RiskCard> deckOfCards, Set<RiskMission> allMissions,
      RiskMission[] playerMissions,
      Map<Integer, List<RiskCard>> playerCards,
      Map<Integer, RiskContinent> continents, int[] nonDeployedReinforcements,
      Collection<Integer> reinforcedTerritories,
      Map<Integer, Integer> involvedTroopsInAttacks, int attackingId,
      int defendingId, int troops, boolean hasOccupiedCountry, RiskPhase phase,
      boolean initialSelectMaybe, boolean initialReinforceMaybe,
      String map) {
    this.numberOfPlayers = numberOfPlayers;
    this.maxAttackerDice = maxAttackerDice;
    this.maxDefenderDice = maxDefenderDice;
    this.withCards = withCards;
    this.tradeInBonus = tradeInBonus;
    this.maxExtraBonus = maxExtraBonus;
    this.tradeIns = tradeIns;
    this.cardTypesWithoutJoker = cardTypesWithoutJoker;
    this.reinforcementAtLeast = reinforcementAtLeast;
    this.reinforcementThreshold = reinforcementThreshold;
    this.occupyOnlyWithAttackingArmies = occupyOnlyWithAttackingArmies;
    this.fortifyOnlyFromSingleTerritory = fortifyOnlyFromSingleTerritory;
    this.fortifyOnlyWithNonFightingArmies = fortifyOnlyWithNonFightingArmies;
    this.withMissions = withMissions;
    this.gameBoard = gameBoard;
    this.territories = Collections.unmodifiableMap(copyTerritories(territories));
    if (fortifyOnlyFromSingleTerritory) {
      this.fortifyConnectivityGraph = fortifyConnectivityGraph;
      this.fortifyConnectivityInspector = fortifyConnectivityInspector;
    } else {
      this.fortifyConnectivityGraph = new HashMap<>(fortifyConnectivityGraph.size() + 1, 1.00f);
      for (int p = 0; p < numberOfPlayers; p++) {
        this.fortifyConnectivityGraph.put(p, new SimpleGraph<>(DefaultEdge.class));
      }
      for (Integer vertex : gameBoard.vertexSet()) {
        for (int p = 0; p < numberOfPlayers; p++) {
          this.fortifyConnectivityGraph.get(p).addVertex(vertex);
        }
      }
      for (DefaultEdge edge : this.gameBoard.edgeSet()) {
        int src = this.gameBoard.getEdgeSource(edge);
        int dst = this.gameBoard.getEdgeTarget(edge);
        int occupant;
        if ((occupant = getTerritoryOccupantId(src)) == getTerritoryOccupantId(dst)) {
          if (occupant < 0) {
            break;
          }
          this.fortifyConnectivityGraph.get(occupant).addEdge(src, dst);
        }
      }

      this.fortifyConnectivityInspector = new HashMap<>(this.fortifyConnectivityGraph.size() + 1,
          1.00f);
      for (Entry<Integer, Graph<Integer, DefaultEdge>> entry : fortifyConnectivityGraph
          .entrySet()) {
        this.fortifyConnectivityInspector
            .put(entry.getKey(), new ConnectivityInspector<>(entry.getValue()));
      }

    }

    this.deckOfCards = deckOfCards != null ? new ArrayDeque<>(deckOfCards) : null;
    this.allMissions = allMissions;
    this.playerMissions = playerMissions != null ? playerMissions.clone() : null;
    if (playerCards == null) {
      this.playerCards = null;
    } else {
      this.playerCards = new HashMap<>(1 + (int) (0.75f * playerCards.size()), 0.75f);
      for (Entry<Integer, List<RiskCard>> entry : playerCards.entrySet()) {
        this.playerCards.put(entry.getKey(), new ArrayList<>(entry.getValue()));
      }
    }
    this.continents = continents;
    this.nonDeployedReinforcements = nonDeployedReinforcements.clone();
    this.reinforcedTerritories = new HashSet<>(reinforcedTerritories);
    this.involvedTroopsInAttacks = new HashMap<>(involvedTroopsInAttacks);
    this.attackingId = attackingId;
    this.defendingId = defendingId;
    this.troops = troops;
    this.hasOccupiedCountry = hasOccupiedCountry;
    this.phase = phase;
    this.initialSelectMaybe = initialSelectMaybe;
    this.initialReinforceMaybe = initialReinforceMaybe;
    this.map = map;
  }

  private static Map<Integer, RiskTerritory> copyTerritories(
      Map<Integer, RiskTerritory> territories) {
    return territories.keySet().stream().collect(Collectors
        .toMap(i -> i, i -> new RiskTerritory(territories.get(i)), (a, b) -> b));
  }

  private static boolean allInRange(int[] array, int[] sizes, int j, int w) {
    if (array.length < sizes.length) {
      return false;
    }

    for (int i = 0; i < sizes.length; i++) {
      if (array[i] >= sizes[i] && array[i] != j && array[i] != w) {
        return false;
      }
    }
    return true;
  }

  private static void selectRandomMissions(List<RiskMission> missionList,
      RiskMission[] playerMissions) {
    Optional<RiskMission> fallbackOptional = missionList.stream()
        .filter(m -> m.getRiskMissionType() == RiskMissionType.OCCUPY_TERRITORY).findFirst();

    if (fallbackOptional.isEmpty()) {
      System.err
          .println("Warning: No fallback (any OCCUPY_TERRITORY) mission could be determined."
              + " Mission-dealing could take a while");

      if (missionList.size() < playerMissions.length) {
        throw new IllegalArgumentException("More players then missions");
      }
    } else {
      for (int i = missionList.size() - 1; i < playerMissions.length; i++) {
        missionList.add(fallbackOptional.get());
      }
    }

    boolean playerLiberateThemselves;
    do {
      playerLiberateThemselves = false;
      Collections.shuffle(missionList);
      for (int i = 0; i < playerMissions.length; i++) {
        RiskMission riskMission = missionList.get(i);
        playerMissions[i] = riskMission;
        playerLiberateThemselves = playerLiberateThemselves
            || (riskMission.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER
            && riskMission.getTargetIds().contains(i));
      }
    } while (fallbackOptional.isEmpty() && playerLiberateThemselves);

    if (fallbackOptional.isPresent() && playerLiberateThemselves) {
      for (int i = 0; i < playerMissions.length; i++) {
        if (playerMissions[i].getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER
            && playerMissions[i].getTargetIds().contains(i)) {
          playerMissions[i] = fallbackOptional.get();
        }
      }
    }

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
    return territories.containsKey(territoryId) ? territories.get(territoryId)
        .getOccupantPlayerId()
        : -1;
  }

  private void setTerritoryOccupantId(int territoryOccupantId, int playerId) {
    if (isTerritory(territoryOccupantId)) {
      territories.get(territoryOccupantId).setOccupantPlayerId(playerId);
    }
  }

  public int getTerritoryTroops(int territoryId) {
    return territories.containsKey(territoryId) ? territories.get(territoryId).getTroops() : 0;
  }

  String getMap() {
    return map;
  }

  boolean isInitialSelectMaybe() {
    return initialSelectMaybe;
  }

  boolean isInitialReinforceMaybe() {
    return initialReinforceMaybe;
  }

  void disableInitialSelectMaybe() {
    initialSelectMaybe = false;
  }

  void disableInitialReinforceMaybe() {
    initialReinforceMaybe = false;
  }

  void initialSelect(int selected, int playerId) {
    RiskTerritory territory = territories.get(selected);
    territory.setOccupantPlayerId(playerId);
    territory.setTroops(1);
    nonDeployedReinforcements[playerId]--;
  }

  void endMove(int nextPlayer) {
    phase = RiskPhase.REINFORCEMENT;
    involvedTroopsInAttacks.clear();
    hasOccupiedCountry = false;
    awardReinforcements(nextPlayer);
    reinforcedTerritories.clear();
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

  boolean areReinforcementsLeft() {
    for (int nonDeployedReinforcement : nonDeployedReinforcements) {
      if (nonDeployedReinforcement > 0) {
        return true;
      }
    }
    return false;
  }

  int reinforcementsLeft(int player) {
    return nonDeployedReinforcements[player];
  }

  boolean isReinforcementPhase() {
    return phase == RiskPhase.REINFORCEMENT;
  }

  boolean isAttackPhase() {
    return phase == RiskPhase.ATTACK;
  }

  boolean isOccupyPhase() {
    return phase == RiskPhase.OCCUPY;
  }

  boolean isFortifyPhase() {
    return phase == RiskPhase.FORTIFY;
  }

  int getMaxAttackerDice() {
    return maxAttackerDice;
  }

  int getMaxDefenderDice() {
    return maxDefenderDice;
  }

  int getNrOfDefenderDice() {

    if (!territories.containsKey(defendingId)) {
      return 0;
    }

    return Math.min(maxDefenderDice, territories.get(defendingId).getTroops());
  }

  int getNrOfAttackerDice() {

    if (!territories.containsKey(attackingId)) {
      return 0;
    }

    return Math.min(maxAttackerDice, troops);
  }

  void reinforce(int player, int reinforcedId, int troops) {
    if (territories.containsKey(reinforcedId)) {
      RiskTerritory territory = territories.get(reinforcedId);
      territory.setTroops(territory.getTroops() + troops);
      nonDeployedReinforcements[player] -= troops;
      reinforcedTerritories.add(reinforcedId);
    }
  }

  boolean isReinforcedAlready(int reinforcedId) {
    return reinforcedTerritories.contains(reinforcedId);
  }

  void endReinforcementPhase() {
    phase = RiskPhase.ATTACK;
    reinforcedTerritories.clear();
  }

  public Set<Integer> neighboringTerritories(int territoryId) {
    return Graphs.neighborSetOf(gameBoard, territoryId);
  }

  public Set<Integer> neighboringEnemyTerritories(int territoryId) {
    final int self = getTerritoryOccupantId(territoryId);
    return Graphs.neighborSetOf(gameBoard, territoryId).stream()
        .filter(id -> getTerritoryOccupantId(id) != self).collect(Collectors.toSet());
  }

  public Set<Integer> neighboringFriendlyTerritories(int territoryId) {
    final int self = getTerritoryOccupantId(territoryId);
    return Graphs.neighborSetOf(gameBoard, territoryId).stream()
        .filter(id -> getTerritoryOccupantId(id) == self).collect(Collectors.toSet());
  }

  public int getMobileTroops(int territoryId) {
    return getTerritoryTroops(territoryId) - 1;
  }

  public int getMaxAttackingTroops(int attackingId) {
    int troops = getMobileTroops(attackingId);
    if (occupyOnlyWithAttackingArmies) {
      return troops;
    }

    return Math.min(troops, getMaxAttackerDice());
  }

  public boolean areNeighbors(int territoryId1, int territoryId2) {
    return gameBoard.containsEdge(territoryId1, territoryId2);
  }

  public Set<Integer> getTerritoriesOccupiedByPlayer(final int playerId) {
    return territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId).map(Entry::getKey)
        .collect(Collectors.toSet());
  }

  public int getNrOfTerritoriesOccupiedByPlayer(final int playerId) {
    return Math.toIntExact(territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId).count());
  }

  public boolean isPlayerStillAlive(final int playerId) {
    return territories.values().stream().anyMatch(t -> t.getOccupantPlayerId() == playerId);
  }

  public Set<Integer> getTerritoriesOccupiedByPlayerWithMoreThanOneTroops(final int playerId) {
    return territories.entrySet().stream()
        .filter(entry -> entry.getValue().getOccupantPlayerId() == playerId
            && entry.getValue().getTroops() > 1).map(Entry::getKey)
        .collect(Collectors.toSet());
  }

  void startAttack(int attackingId, int defendingId, int troops) {
    this.attackingId = attackingId;
    this.defendingId = defendingId;
    this.troops = troops;
  }

  boolean isAttack() {
    return phase == RiskPhase.ATTACK && attackingId >= 0 && defendingId >= 0 && troops > 0;
  }

  int endAttack(int attackerCasualties, int defendingCasualties) {
    int attackerId = getTerritoryOccupantId(attackingId);
    if (isAttack()) {
      territories.get(attackingId).removeTroops(attackerCasualties);
      territories.get(defendingId).removeTroops(defendingCasualties);
      troops -= attackerCasualties;
      involvedTroopsInAttacks.compute(attackingId,
          (k, v) -> (v == null) ? (troops)
              : Math.max(v, troops));

      if (getTerritoryTroops(defendingId) == 0) {
        setTerritoryOccupantId(defendingId, getTerritoryOccupantId(attackingId));
        phase = RiskPhase.OCCUPY;
        hasOccupiedCountry = true;
        if (occupyOnlyWithAttackingArmies) {
          occupy(troops);
        }
      } else {
        attackingId = -1;
        defendingId = -1;
        troops = 0;
      }
    }
    return attackerId;
  }

  void endAttackPhase() {
    phase = RiskPhase.FORTIFY;
    attackingId = -1;
    defendingId = -1;
    troops = 0;
  }

  public int getMaxOccupy() {
    if (occupyOnlyWithAttackingArmies) {
      return troops;
    }

    return getMobileTroops(attackingId);
  }

  void occupy(int troops) {
    territories.get(attackingId).removeTroops(troops);
    territories.get(defendingId).addTroops(troops);
    involvedTroopsInAttacks
        .compute(attackingId, (k, v) -> v == null ? 0 : Math.max(0, v - troops));
    involvedTroopsInAttacks.compute(defendingId, (k, v) -> v == null ? troops : v + troops);
    if (!fortifyOnlyFromSingleTerritory) {
      int attackerId = getTerritoryOccupantId(attackingId);
      for (Integer neighbor : neighboringFriendlyTerritories(defendingId)) {
        DefaultEdge edge = fortifyConnectivityGraph.get(attackerId).addEdge(neighbor, defendingId);
        fortifyConnectivityInspector.get(attackerId).edgeAdded(
            new GraphEdgeChangeEvent<>(this, GraphEdgeChangeEvent.EDGE_ADDED, edge, neighbor,
                defendingId));
      }
    }
    attackingId = -1;
    defendingId = -1;
    this.troops = 0;
    phase = RiskPhase.ATTACK;
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

  private PriestLogic missionFulfilled(RiskMission mission) {
    if (mission.getRiskMissionType() == RiskMissionType.LIBERATE_PLAYER) {
      return PriestLogic
          .fromBoolean(mission.getTargetIds().stream().noneMatch(this::isPlayerStillAlive));
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
    Set<Integer> occupiedTerritories = getTerritoriesOccupiedByPlayer(player);

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

  public Set<Integer> getFortifyableTerritories(int territoryId) {
    if (fortifyOnlyFromSingleTerritory) {
      return neighboringFriendlyTerritories(territoryId);
    }

    int player = getTerritoryOccupantId(territoryId);
    Set<Integer> fortifyableTerritories = fortifyConnectivityInspector.get(player)
        .connectedSetOf(territoryId);
    fortifyableTerritories.remove(territoryId);
    return fortifyableTerritories;
  }

  public boolean canFortify(int fortifyingId, int fortifiedId) {
    int occupant = getTerritoryOccupantId(fortifyingId);
    return occupant >= 0 && occupant == getTerritoryOccupantId(fortifiedId)
        && (fortifyOnlyFromSingleTerritory || fortifyConnectivityInspector.get(occupant)
        .pathExists(fortifyingId, fortifiedId))
        && (!fortifyOnlyFromSingleTerritory || areNeighbors(fortifyingId, fortifiedId));
  }

  void fortify(int fortifyingId, int fortifiedId, int troops) {
    territories.get(fortifyingId).removeTroops(troops);
    territories.get(fortifiedId).addTroops(troops);
  }

  boolean isFortifyOnlyFromSingleTerritory() {
    return fortifyOnlyFromSingleTerritory;
  }

  public int getFortifyableTroops(int territoryId) {
    int troops = getTerritoryTroops(territoryId);
    if (fortifyOnlyWithNonFightingArmies) {
      troops -= involvedTroopsInAttacks.get(territoryId);
    }

    return Math.min(troops, getMobileTroops(territoryId));
  }

  public PriestLogic canTradeInCards(int player) {
    PriestLogic hasCorrectCards = PriestLogic.FALSE;
    if (playerCards.containsKey(player)) {
      Collection<RiskCard> cards = this.playerCards.get(player);
      int size = cards.size();
      boolean hasEnoughCards = size >= cardTypesWithoutJoker;
      hasCorrectCards = PriestLogic.fromBoolean(hasEnoughCards);
      if (hasEnoughCards && size < cardSlots() - 1) {
        Map<Integer, Integer> numberOfCards = IntStream.rangeClosed(0, cardTypesWithoutJoker)
            .boxed().collect(Collectors.toMap(i -> i, i -> 0));
        for (RiskCard card : cards) {
          if (card.getCardType() != RiskCard.WILDCARD) {
            numberOfCards.compute(card.getCardType(), (k, v) -> v != null ? v + 1 : 1);
          } else {
            hasCorrectCards = PriestLogic.UNKNOWN;
          }
        }

        hasCorrectCards = PriestLogic
            .implies(hasCorrectCards, hasOneOfEach(numberOfCards) || hasAllOfOne(numberOfCards));
      }
    }
    return hasCorrectCards;
  }

  public boolean couldTradeInCards(int player) {
    return PriestLogic.possible(canTradeInCards(player));
  }

  public boolean hasToTradeInCards(int player) {
    return playerCards.containsKey(player)
        && playerCards.get(player).size() >= cardSlots();
  }

  private boolean hasOneOfEach(Iterable<RiskCard> cards) {
    return hasOneOfEach(numberOfCards(cards));
  }

  private boolean hasAllOfOne(Iterable<RiskCard> cards) {
    return hasAllOfOne(numberOfCards(cards));
  }

  private Map<Integer, Integer> numberOfCards(Iterable<RiskCard> cards) {
    Map<Integer, Integer> numberOfCards = new HashMap<>();
    for (RiskCard card : cards) {
      numberOfCards.compute(card.getCardType(), (k, v) -> v == null ? 1 : v + 1);
    }
    return numberOfCards;
  }

  private boolean hasOneOfEach(Map<Integer, Integer> numberOfCards) {
    final int numberOfJokers = numberOfCards.getOrDefault(RiskCard.JOKER, 0);
    return numberOfCards.entrySet().stream().filter(
        e -> e.getValue() > 0
            && e.getKey() != RiskCard.JOKER
            && e.getKey() != RiskCard.WILDCARD)
        .count() + numberOfJokers >= cardTypesWithoutJoker;
  }

  private boolean hasAllOfOne(Map<Integer, Integer> numberOfCards) {
    final int numberOfJokers = numberOfCards.getOrDefault(RiskCard.JOKER, 0);
    return numberOfCards.entrySet().stream().filter(
        e -> e.getValue() > 0
            && e.getKey() != RiskCard.JOKER
            && e.getKey() != RiskCard.WILDCARD)
        .anyMatch(e -> e.getValue() + numberOfJokers >= cardTypesWithoutJoker);
  }

  Set<Integer> getTradeInOptionSlots(int player) {
    List<RiskCard> playerCards = this.playerCards.getOrDefault(player, Collections.emptyList());
    List<List<RiskCard>> tradeInOptions = getTradeInOptions(playerCards);

    return tradeInOptions.stream().map(l -> {
      int n = 0;
      for (RiskCard riskCard : l) {
        n |= 1 << (playerCards.indexOf(riskCard));
      }
      return n;
    }).collect(Collectors.toSet());
  }

  List<List<RiskCard>> getTradeInOptions(int player) {
    return getTradeInOptions(playerCards.getOrDefault(player, Collections.emptyList()));
  }

  private List<List<RiskCard>> getTradeInOptions(Collection<RiskCard> playerCards) {
    if (playerCards.size() < cardTypesWithoutJoker) {
      return Collections.emptyList();
    }
    List<List<RiskCard>> options = new ArrayList<>();
    Map<Integer, List<RiskCard>> separatedPlayerCards = mapToCardTypes(playerCards);
    if (separatedPlayerCards.getOrDefault(RiskCard.WILDCARD, Collections.emptyList()).size()
        + separatedPlayerCards.getOrDefault(RiskCard.JOKER, Collections.emptyList()).size()
        == playerCards.size()) {
      return Util.combinations(playerCards, numberOfPlayers).stream().map(Util::asList)
          .collect(Collectors.toList());
    }
    options.addAll(getTradeInOptionsAllOfOne(separatedPlayerCards));
    options.addAll(getTradeInOptionsOneOfAll(separatedPlayerCards));
    return options;
  }

  private Map<Integer, List<RiskCard>> mapToCardTypes(
      final Collection<RiskCard> playerCards) {
    return IntStream
        .concat(IntStream.of(RiskCard.WILDCARD), IntStream.rangeClosed(0, cardTypesWithoutJoker))
        .boxed().
            collect(Collectors.toMap(
                i -> i,
                i -> playerCards.stream().filter(c -> c.getCardType() == i)
                    .collect(Collectors.toList())));
  }

  private Collection<List<RiskCard>> getTradeInOptionsAllOfOne(
      Collection<RiskCard> playerCards) {
    return getTradeInOptionsAllOfOne(mapToCardTypes(playerCards));
  }

  private Collection<List<RiskCard>> getTradeInOptionsAllOfOne(
      Map<Integer, List<RiskCard>> separatedPlayerCards) {

    Collection<RiskCard> wildcards = separatedPlayerCards
        .getOrDefault(RiskCard.WILDCARD, Collections.emptyList());
    Collection<RiskCard> jokers = separatedPlayerCards
        .getOrDefault(RiskCard.JOKER, Collections.emptyList());

    final int wildcardsSize = wildcards.size();
    final int jokersSize = jokers.size();

    Stream<Entry<Integer, List<RiskCard>>> stream = separatedPlayerCards.entrySet().stream()
        .filter(e -> e.getKey() != RiskCard.WILDCARD && e.getKey() != RiskCard.JOKER);

    stream = stream
        .filter(e -> e.getValue().size() + wildcardsSize + jokersSize >= cardTypesWithoutJoker);

    Collection<Collection<RiskCard>> cards = stream.map(Entry::getValue)
        .collect(Collectors.toList());

    cards.forEach(e -> {
      e.addAll(wildcards);
      e.addAll(jokers);
    });

    Collection<List<RiskCard>> tradeInOptions = new ArrayList<>();
    for (Collection<RiskCard> riskCards : cards) {
      tradeInOptions.addAll(
          Util.combinations(riskCards, cardTypesWithoutJoker).stream().map(Util::asList).collect(
              Collectors.toList()));
    }

    return tradeInOptions;
  }

  private Collection<List<RiskCard>> getTradeInOptionsOneOfAll(
      Collection<RiskCard> playerCards) {
    return getTradeInOptionsOneOfAll(mapToCardTypes(playerCards));
  }

  private Collection<List<RiskCard>> getTradeInOptionsOneOfAll(
      Map<Integer, List<RiskCard>> separatedPlayerCards) {

    List<RiskCard> wildcards = separatedPlayerCards
        .getOrDefault(RiskCard.WILDCARD, Collections.emptyList());
    List<RiskCard> jokers = separatedPlayerCards
        .getOrDefault(RiskCard.JOKER, Collections.emptyList());

    final int wildcardsSize = wildcards.size();
    final int jokersSize = jokers.size();

    Stream<Entry<Integer, List<RiskCard>>> stream = separatedPlayerCards.entrySet().stream()
        .filter(
            e -> e.getKey() != RiskCard.WILDCARD && e.getKey() != RiskCard.JOKER);

    List<List<RiskCard>> sepCards = stream.map(e -> Util.asList(e.getValue()))
        .collect(Collectors.toList());

    final int sepCardsSize = (int) sepCards.stream().filter(s -> !s.isEmpty()).count();
    if (sepCardsSize + wildcardsSize + jokersSize < cardTypesWithoutJoker) {
      return Collections.emptySet();
    }

    if (sepCardsSize + jokersSize <= 0) {
      return Util.asList(
          Util.combinations(wildcards, cardTypesWithoutJoker).stream().map(Util::asList).collect(
              Collectors.toList()));
    }

    int[] sizes = sepCards.stream().mapToInt(Collection::size).toArray();
    final int m = Arrays.stream(sizes).max().orElse(0);
    final int r = m + (jokersSize > 0 ? 1 : 0) + (wildcardsSize > 0 ? 1 : 0);
    final int j = m + 1;
    final int w = m + 2;

    int[] indices = new int[cardTypesWithoutJoker];
    Arrays.fill(indices, 0);
    while (!allInRange(indices, sizes, j, w)
        || Util.numberOfEqualTo(indices, j) > jokersSize
        || Util.numberOfEqualTo(indices, w) > wildcardsSize) {
      indices = Util.permutations(indices, r);
    }

    Collection<List<RiskCard>> options = new ArrayList<>();
    Collection<RiskCard> option = new ArrayList<>(cardTypesWithoutJoker);

    do {
      option.clear();

      for (int index : indices) {
        if (index == j) {
          option.add(RiskCard.joker);
        } else if (index == w) {
          option.add(RiskCard.wildcard);
        } else {
          option.add(sepCards.get(option.size()).get(index));
        }
      }

      options.add(List.copyOf(option));

      //skips permutations which are not possible (due to being out of range or not enough cards)
      do {
        indices = Util.permutations(indices, r);
      } while (!Util.allEqualTo(indices, 0) && (!allInRange(indices, sizes, j, w)
          || Util.numberOfEqualTo(indices, j) > jokersSize
          || Util.numberOfEqualTo(indices, w) > wildcardsSize));

    } while (!Util.allEqualTo(indices, 0));

    return options;
  }

  public List<RiskCard> getPlayerCards(int player) {
    return Collections
        .unmodifiableList(playerCards.getOrDefault(player, Collections.emptyList()));
  }

  private int cardSlots() {
    return cardSlots(cardTypesWithoutJoker);
  }

  private int cardSlots(int cardTypesWithoutJoker) {
    cardTypesWithoutJoker--;
    return cardTypesWithoutJoker * cardTypesWithoutJoker + 1;
  }

  boolean allowedToTradeIn(int player) {
    return isReinforcementPhase()
        || playerCards.getOrDefault(player, Collections.emptyList()).size() >= cardSlots();
  }

  public int getTradeInBonus(int n) {
    if (n < 0) {
      return 0;
    }
    if (n >= tradeInBonus.length) {
      return tradeInBonus[tradeInBonus.length - 1] + (n - tradeInBonus.length + 1)
          * maxExtraBonus;
    }
    return tradeInBonus[n];
  }

  public int getTradeInBonus() {
    return getTradeInBonus(tradeIns);
  }

  boolean canTradeInCardIds(final int player, Set<Integer> cardIds) {
    final List<RiskCard> playerCards = this.playerCards
        .getOrDefault(player, Collections.emptyList());
    final int cardsInHand = playerCards.size();
    if (cardIds.size() != cardTypesWithoutJoker || cardIds.stream()
        .anyMatch(c -> c >= cardsInHand)) {
      return false;
    }
    List<RiskCard> toTradeIn = cardIds.stream().map(playerCards::get).collect(Collectors.toList());
    Map<Integer, Integer> numberOfCards = numberOfCards(toTradeIn);

    final int numberOfWildcards = numberOfCards.getOrDefault(RiskCard.WILDCARD, 0);
    final int numberOfJokers = numberOfCards.getOrDefault(RiskCard.JOKER, 0);
    return numberOfCards.entrySet().stream()
        .filter(e -> e.getKey() != RiskCard.WILDCARD && e.getKey() != RiskCard.JOKER)
        .map(Entry::getValue)
        .anyMatch(v -> v + numberOfWildcards + numberOfJokers == cardTypesWithoutJoker)
        || numberOfCards.keySet().stream()
        .filter(k -> k != RiskCard.WILDCARD && k != RiskCard.JOKER).distinct().count()
        + numberOfWildcards + numberOfJokers
        == cardTypesWithoutJoker;
  }

  void tradeIn(int player, Set<Integer> cardIds) {
    Deque<RiskCard> cards = cardIds.stream()
        .map(i -> playerCards.get(player).get(i)).collect(Collectors.toCollection(LinkedList::new));
    playerCards.get(player).removeAll(cards);
    Util.shuffle(cards);
    this.deckOfCards.addAll(cards);
    this.nonDeployedReinforcements[player] += getTradeInBonus();
    tradeIns++;
    if (phase != RiskPhase.REINFORCEMENT) {
      reinforcedTerritories.clear();
    }
    phase = RiskPhase.REINFORCEMENT;
  }

  void drawCardIfPossible(int player) {
    if (withCards && hasOccupiedCountry && playerCards.containsKey(player)) {
      playerCards.get(player).add(deckOfCards.pop());
    }
  }

  private enum RiskPhase {
    REINFORCEMENT,
    ATTACK,
    OCCUPY,
    FORTIFY,
  }

}
