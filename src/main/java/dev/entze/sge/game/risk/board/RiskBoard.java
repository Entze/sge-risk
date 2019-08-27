package dev.entze.sge.game.risk.board;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
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
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class RiskBoard {

  private static final Set<Set<Integer>> TRADE_IN_3_OUT_OF_5 = Set
      .of(Set.of(0, 1, 2), Set.of(0, 1, 3), Set.of(0, 2, 3), Set.of(1, 2, 3), Set.of(0, 1, 4),
          Set.of(0, 2, 4), Set.of(1, 2, 4), Set.of(0, 3, 4), Set.of(1, 3, 4), Set.of(2, 3, 4));

  private static final Set<Set<Integer>> TRADE_IN_3_OUT_OF_4 = Set
      .of(Set.of(0, 1, 2), Set.of(0, 1, 3), Set.of(0, 2, 3), Set.of(1, 2, 3));

  private static final Set<Set<Integer>> TRADE_IN_3_OUT_OF_3 = Set.of(Set.of(0, 1, 2));

  private static final Set<Multiset<Integer>> SETS_3_OUT_OF_5 = Set.of(
      ImmutableMultiset.of(-1, -1, -1),
      ImmutableMultiset.of(1, 1, 1), ImmutableMultiset.of(2, 2, 2), ImmutableMultiset.of(3, 3, 3),
      ImmutableMultiset.of(0, 1, 1), ImmutableMultiset.of(0, 2, 2), ImmutableMultiset.of(0, 3, 3),
      ImmutableMultiset.of(1, 2, 3),
      ImmutableMultiset.of(0, 2, 3), ImmutableMultiset.of(0, 1, 3), ImmutableMultiset.of(0, 1, 2),
      ImmutableMultiset.of(-1, 1, 1), ImmutableMultiset.of(-1, 2, 2),
      ImmutableMultiset.of(-1, 3, 3),
      ImmutableMultiset.of(-1, 0, 1), ImmutableMultiset.of(-1, 0, 2),
      ImmutableMultiset.of(-1, 0, 3),
      ImmutableMultiset.of(-1, 2, 3), ImmutableMultiset.of(-1, 1, 3),
      ImmutableMultiset.of(-1, 1, 2),
      ImmutableMultiset.of(-1, -1, 1), ImmutableMultiset.of(-1, -1, 2),
      ImmutableMultiset.of(-1, -1, 3),
      ImmutableMultiset.of(-1, -1, 0));

  //settings
  private final int numberOfPlayers;
  private final int maxAttackerDice;
  private final int maxDefenderDice;

  private final boolean withCards;
  private final int[] tradeInBonus;
  private final int tradeInTerritoryBonus = 2;
  private Set<Integer> tradeInTerritories;
  private int minMatchingTerritories;
  private int maxMatchingTerritories;
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
  private final List<RiskCard> discardPile;
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

  private int tradedInId;
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
      discardPile = Collections.emptyList();
      playerCards = IntStream.range(0, numberOfPlayers).boxed().collect(Collectors
          .toUnmodifiableMap(p -> p, p -> new ArrayList<>(cardSlots())));

      tradeInTerritories = Collections.emptySet();
    } else {
      tradeInBonus = null;
      maxExtraBonus = 0;
      deckOfCards = null;
      playerCards = null;
      discardPile = null;
      tradeInTerritories = null;
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

    tradedInId = -5;
    minMatchingTerritories = 0;
    maxMatchingTerritories = 0;

    map = configuration.getMap();
  }

  RiskBoard(RiskBoard riskBoard) {
    this(riskBoard.numberOfPlayers, riskBoard.maxAttackerDice, riskBoard.maxDefenderDice,
        riskBoard.withCards, riskBoard.tradeInBonus, riskBoard.maxExtraBonus, riskBoard.tradeIns,
        riskBoard.cardTypesWithoutJoker, riskBoard.reinforcementAtLeast,
        riskBoard.reinforcementThreshold, riskBoard.occupyOnlyWithAttackingArmies,
        riskBoard.fortifyOnlyFromSingleTerritory, riskBoard.fortifyOnlyWithNonFightingArmies,
        riskBoard.withMissions, riskBoard.gameBoard, riskBoard.territories,
        riskBoard.fortifyConnectivityGraph, riskBoard.fortifyConnectivityInspector,
        riskBoard.deckOfCards, riskBoard.discardPile, riskBoard.allMissions,
        riskBoard.playerMissions, riskBoard.playerCards, riskBoard.continents,
        riskBoard.nonDeployedReinforcements, riskBoard.reinforcedTerritories,
        riskBoard.involvedTroopsInAttacks, riskBoard.attackingId, riskBoard.defendingId,
        riskBoard.troops, riskBoard.hasOccupiedCountry, riskBoard.phase,
        riskBoard.initialSelectMaybe, riskBoard.initialReinforceMaybe, riskBoard.tradedInId,
        riskBoard.tradeInTerritories, riskBoard.minMatchingTerritories,
        riskBoard.maxMatchingTerritories, riskBoard.map);
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
      Collection<RiskCard> deckOfCards, Collection<RiskCard> discardPile,
      Set<RiskMission> allMissions,
      RiskMission[] playerMissions,
      Map<Integer, List<RiskCard>> playerCards,
      Map<Integer, RiskContinent> continents, int[] nonDeployedReinforcements,
      Collection<Integer> reinforcedTerritories,
      Map<Integer, Integer> involvedTroopsInAttacks, int attackingId,
      int defendingId, int troops, boolean hasOccupiedCountry, RiskPhase phase,
      boolean initialSelectMaybe, boolean initialReinforceMaybe, int tradedInId,
      Set<Integer> tradeInTerritories, int minMatchingTerritories, int maxMatchingTerritories,
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
    this.discardPile = discardPile != null ? new ArrayList<>(discardPile) : null;
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
    this.tradedInId = tradedInId;
    this.tradeInTerritories = tradeInTerritories != null ? new HashSet<>(tradeInTerritories) : null;
    this.minMatchingTerritories = Math
        .max(0, Math.min(minMatchingTerritories, cardTypesWithoutJoker));
    this.maxMatchingTerritories = Math
        .max(this.minMatchingTerritories, Math.min(maxMatchingTerritories, cardTypesWithoutJoker));
    this.map = map;
  }

  private static Map<Integer, RiskTerritory> copyTerritories(
      Map<Integer, RiskTerritory> territories) {
    return territories.keySet().stream().collect(Collectors
        .toMap(i -> i, i -> new RiskTerritory(territories.get(i)), (a, b) -> b));
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
    tradeInTerritories.clear();
    tradedInId = -5;
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

  private boolean containsTradeableSet(Multiset<Integer> cardTypes) {
    if (cardTypes.size() < cardTypesWithoutJoker) {
      return false;
    }
    int jokers = cardTypes.contains(RiskCard.JOKER) ? 1 : 0;

    int wildcards = cardTypes.count(RiskCard.WILDCARD);

    if (jokers + wildcards >= cardTypesWithoutJoker) {
      return true;
    }

    int[] count = IntStream.rangeClosed(1, cardTypesWithoutJoker).map(cardTypes::count).toArray();

    int mostCards = Arrays.stream(count).max().orElse(0);

    if (mostCards + jokers + wildcards >= cardTypesWithoutJoker) {
      return true;
    }

    int distinctCards = Arrays.stream(count).map(i -> i <= 0 ? 0 : 1).sum();
    return distinctCards + jokers + wildcards >= cardTypesWithoutJoker;
  }

  private boolean containsTradeableSet(List<RiskCard> playerCards) {
    if (!withCards || playerCards.size() < cardTypesWithoutJoker) {
      return false;
    }
    return containsTradeableSet(playerCards.stream().map(RiskCard::getCardType)
        .collect(ImmutableMultiset.toImmutableMultiset()));
  }

  private boolean canTradeInAsSet(Multiset<Integer> cardTypes) {

    int jokers = cardTypes.count(RiskCard.JOKER);

    if (jokers > 1) {
      return false;
    }

    int wildcards = cardTypes.count(RiskCard.WILDCARD);

    if (jokers + wildcards == cardTypesWithoutJoker) {
      return true;
    }

    int[] count = IntStream.rangeClosed(1, cardTypesWithoutJoker).map(cardTypes::count).toArray();

    int mostCards = Arrays.stream(count).max().orElse(0);

    if (mostCards + jokers + wildcards == cardTypesWithoutJoker) {
      return true;
    }

    int distinctCards = Arrays.stream(count).map(i -> i <= 0 ? 0 : 1).sum();
    return distinctCards + jokers + wildcards == cardTypesWithoutJoker;
  }

  private boolean canTradeInAsSet(List<RiskCard> playerCards) {
    if (playerCards.size() != cardTypesWithoutJoker) {
      return false;
    }

    return canTradeInAsSet(playerCards.stream().map(RiskCard::getCardType)
        .collect(ImmutableMultiset.toImmutableMultiset()));

  }

  boolean canTradeInAsSet(Set<Integer> slotIds, int player) {
    if (!withCards || playerCards == null || !playerCards.containsKey(player)) {
      return false;
    }
    return canTradeInAsSet(slotIds.stream().map(i -> playerCards.get(player).get(i).getCardType())
        .collect(ImmutableMultiset.toImmutableMultiset()));
  }

  public boolean couldTradeInCards(int player) {
    if (!withCards || playerCards == null || !playerCards.containsKey(player)) {
      return false;
    }

    List<RiskCard> riskCards = getPlayerCards(player);
    return containsTradeableSet(riskCards);
  }

  public boolean hasToTradeInCards(int player) {
    return withCards && playerCards.containsKey(player)
        && playerCards.get(player).size() >= cardSlots();
  }

  private Set<Set<Integer>> getTradeInSlotCandidates(int player) {
    if (!withCards || playerCards == null || !playerCards.containsKey(player)) {
      return Collections.emptySet();
    }
    final int playerCardsSize = playerCards.get(player).size();

    if (cardTypesWithoutJoker == 3) {
      if (playerCardsSize < 3) {
        return Collections.emptySet();
      }
      if (playerCardsSize == 3) {
        return TRADE_IN_3_OUT_OF_3;
      }
      if (playerCardsSize == 4) {
        return TRADE_IN_3_OUT_OF_4;
      }
      return TRADE_IN_3_OUT_OF_5;
    }

    return Sets.combinations(IntStream.range(0, playerCardsSize).boxed().collect(
        Collectors.toSet()), cardTypesWithoutJoker);
  }

  /**
   * Checks if the slot is a tradeable set.
   *
   * @param slotIds - the ids of the set
   * @param player  - the player
   * @return true if it is a valid set
   */
  private boolean checkSlotCandidateDefault(Set<Integer> slotIds, int player) {
    if (!withCards || playerCards == null || !playerCards.containsKey(player)
        || slotIds.size() != cardTypesWithoutJoker) {
      return false;
    }

    ImmutableMultiset<Integer> cardtypes = slotIds.stream()
        .map(i -> playerCards.get(player).get(i).getCardType())
        .collect(ImmutableMultiset.toImmutableMultiset());

    return SETS_3_OUT_OF_5.contains(cardtypes);
  }

  private boolean checkSlotCandidate(Set<Integer> slotIds, int player) {
    if (!withCards || playerCards == null || !playerCards.containsKey(player)
        || slotIds.size() != cardTypesWithoutJoker) {
      return false;
    }

    if (cardTypesWithoutJoker == 3) {
      return checkSlotCandidateDefault(slotIds, player);
    }

    return canTradeInAsSet(slotIds, player);
  }

  Set<Set<Integer>> getTradeInSlots(int player) {
    return getTradeInSlotCandidates(player).stream().filter(c -> checkSlotCandidate(c, player))
        .collect(Collectors.toUnmodifiableSet());
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
    return withCards && (isReinforcementPhase()
        || playerCards.getOrDefault(player, Collections.emptyList()).size() >= cardSlots());
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

  void tradeIn(Set<Integer> cardIds, int player) {
    List<RiskCard> cards = cardIds.stream()
        .map(i -> playerCards.get(player).get(i)).collect(Collectors.toCollection(LinkedList::new));
    this.discardPile.addAll(cards);
    tradeInTerritories = cards.stream().filter(
        c -> c.getCardType() != RiskCard.WILDCARD && c.getCardType() != RiskCard.JOKER
            && getTerritoryOccupantId(c.getTerritoryId()) == player).map(RiskCard::getTerritoryId)
        .collect(Collectors.toUnmodifiableSet());

    Set<Integer> discarded = this.discardPile.stream().map(RiskCard::getTerritoryId).collect(
        Collectors.toUnmodifiableSet());

    long maxPossible = territories.entrySet().stream().filter(
        t -> t.getValue().getOccupantPlayerId() == player && !discarded.contains(t.getKey()))
        .count();

    long numberOfWildcards = cards.stream().filter(c -> c.getCardType() == RiskCard.WILDCARD)
        .count();

    minMatchingTerritories = tradeInTerritories.size();
    maxMatchingTerritories =
        minMatchingTerritories + (int) Math.min(numberOfWildcards, maxPossible);
    this.playerCards.get(player).removeAll(cards);
    reinforcedTerritories.clear();
    phase = RiskPhase.REINFORCEMENT;
    tradedInId = player;
  }

  void awardBonus(int nrOfMatchingTerritories, int player) {
    this.nonDeployedReinforcements[player] +=
        getTradeInBonus() + nrOfMatchingTerritories * tradeInTerritoryBonus;
    tradeIns++;
  }

  public int getMinMatchingTerritories() {
    return minMatchingTerritories;
  }

  public int getMaxMatchingTerritories() {
    return maxMatchingTerritories;
  }

  int getNrOfBonusTerritories() {
    return tradeInTerritories.size();
  }

  boolean inBonusTerritories(int id) {
    return tradeInTerritories.contains(id);
  }

  public int getTradeInTerritoryBonus() {
    return tradeInTerritoryBonus;
  }

  public Set<Integer> getBonusTerritories() {
    return Collections.unmodifiableSet(tradeInTerritories);
  }

  public int getCardsLeft() {
    return deckOfCards.size();
  }

  public Collection<RiskCard> getDiscardedPile() {
    return Collections.unmodifiableList(discardPile);
  }

  public int getNumberOfCards() {
    if (!withCards || playerCards == null || discardPile == null || deckOfCards == null) {
      return 0;
    }
    return playerCards.values().stream().mapToInt(List::size).sum() + discardPile.size()
        + deckOfCards.size();
  }

  int getTradedInId() {
    return tradedInId;
  }

  void drawCardIfPossible(int player) {
    if (withCards && hasOccupiedCountry && playerCards.containsKey(player)) {
      if (deckOfCards.isEmpty()) {
        reshuffle();
      }
      if (!deckOfCards.isEmpty()) {
        playerCards.get(player).add(deckOfCards.pop());
      }
    }
  }

  private void reshuffle() {
    if (withCards && discardPile != null && deckOfCards != null) {
      Collections.shuffle(discardPile);
      deckOfCards.addAll(discardPile);
      discardPile.clear();
    }
  }

  void stripOutUnknownInformation() {
    stripOutCardInformation();
  }

  void stripOutUnknownInformation(int player) {
    stripOutCardInformation(player);
  }

  private void stripOutCardInformation() {
    List<RiskCard> deckOfCards = this.deckOfCards.stream().map(c -> RiskCard.wildcard())
        .collect(Collectors.toCollection(ArrayList::new));

    Collections.shuffle(deckOfCards);

    this.deckOfCards.clear();
    this.deckOfCards.addAll(deckOfCards);

  }


  private void stripOutCardInformation(int player) {
    for (Entry<Integer, List<RiskCard>> playerCard : playerCards.entrySet()) {
      int playerSlot = playerCard.getKey();
      if (playerSlot != player) {
        playerCards.get(playerSlot).replaceAll(c -> RiskCard.wildcard());
      }
    }
  }

  private enum RiskPhase {
    REINFORCEMENT,
    ATTACK,
    OCCUPY,
    FORTIFY,
  }

}
