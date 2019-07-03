package dev.entze.sge.game.risk;

import dev.entze.sge.game.ActionRecord;
import dev.entze.sge.game.Dice;
import dev.entze.sge.game.Game;
import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.board.RiskTerritory;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.util.PriestLogic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Risk implements Game<RiskAction, RiskBoard> {

  private final boolean canonical;
  private int currentPlayerId;
  private List<ActionRecord<RiskAction>> actionRecords;
  private RiskBoard board;

  private final Dice attackerDice;
  private final Dice defenderDice;


  public Risk(String yaml, int numberOfPlayers) {
    this((RiskConfiguration) RiskConfiguration.getYaml().load(yaml), numberOfPlayers);
  }

  public Risk(RiskConfiguration configuration, int numberOfPlayers) {
    this(0, true, Collections.emptyList(), new RiskBoard(configuration, numberOfPlayers));
    if (numberOfPlayers > configuration.getMaxNumberOfPlayers()) {
      throw new IllegalArgumentException("Wrong number of players");
    }
  }

  public Risk(Risk risk) {
    this(risk.currentPlayerId, risk.canonical, risk.actionRecords, risk.board);
    this.initialSelectMaybe = risk.initialSelectMaybe;
    this.initialReinforceMaybe = risk.initialReinforceMaybe;
  }

  public Risk(int currentPlayerId, boolean canonical,
      List<ActionRecord<RiskAction>> actionRecords, RiskBoard board) {
    this.currentPlayerId = currentPlayerId;
    this.canonical = canonical;
    this.actionRecords = new ArrayList<>(actionRecords);
    this.board = new RiskBoard(board);
    if (!(getMinimumNumberOfPlayers() <= getNumberOfPlayers()
        && getNumberOfPlayers() <= getMaximumNumberOfPlayers())) {
      throw new IllegalArgumentException("Wrong number of players");
    }

    this.attackerDice = new Dice(board.getMaxAttackerDice());
    this.defenderDice = new Dice(board.getMaxDefenderDice());
  }

  @Override
  public boolean isGameOver() {
    if (!isInitialSelect() && board.getTerritories().values().stream().mapToInt(
        RiskTerritory::getOccupantPlayerId).distinct().count()
        == 1L) { // all territories belong to one player
      return true;
    }
    return false;
  }

  @Override
  public int getMinimumNumberOfPlayers() {
    return 2;
  }

  @Override
  public int getMaximumNumberOfPlayers() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int getNumberOfPlayers() {
    return board.getNumberOfPlayers();
  }

  @Override
  public int getCurrentPlayer() {
    return currentPlayerId;
  }

  @Override
  public double getUtilityValue(int i) {
    return 0;
  }

  @Override
  public Set<RiskAction> getPossibleActions() {
    if (currentPlayerId < 0) {
      if (board.isAttack()) {
        return casualtiesGPA();
      }
    } else if (isInitialSelect()) {
      return initialSelectGPA();
    } else if (isInitialReinforce()) {
      return initialReinforceGPA();
    } else if (board.isReinforcementPhase()) {
      return reinforceGPA();
    } else if (board.isAttackPhase()) {
      return attackGPA();
    }

    return Collections.emptySet();
  }

  private boolean initialSelectMaybe = true;

  private boolean isInitialSelect() {
    if (initialSelectMaybe && (board.getTerritories().values().stream().anyMatch(
        t -> !(0 <= t.getOccupantPlayerId() && t.getOccupantPlayerId() < getNumberOfPlayers())))) {
      return true;
    }
    initialSelectMaybe = false;
    return false;
  }

  private boolean initialReinforceMaybe = true;

  private boolean isInitialReinforce() {
    if (initialReinforceMaybe && board.areReinforcementsLeft()) {
      return true;
    }
    initialReinforceMaybe = false;
    return false;
  }

  private boolean couldMissionBeDone() {
    return couldMissionBeDone(currentPlayerId);
  }

  private boolean couldMissionBeDone(int player) {
    return PriestLogic.possible(board.missionFulfilled(player));
  }

  private boolean isMissionDone() {
    return isMissionDone(currentPlayerId);
  }

  private boolean isMissionDone(int player) {
    return PriestLogic.valid(board.missionFulfilled(player));
  }

  private Set<RiskAction> initialSelectGPA() {
    return board.getTerritories().entrySet().stream().filter(
        t -> !(0 <= t.getValue().getOccupantPlayerId()
            && t.getValue().getOccupantPlayerId() < getNumberOfPlayers())).mapToInt(Entry::getKey)
        .mapToObj(RiskAction::select).collect(Collectors.toSet());
  }

  private Set<RiskAction> initialReinforceGPA() {
    return board.occupiedTerritoriesByPlayer(this.currentPlayerId).stream()
        .map(id -> RiskAction.reinforce(id, 1)).collect(Collectors.toSet());
  }

  private Set<RiskAction> reinforceGPA() {
    Set<RiskAction> actions = new HashSet<>();
    int reinforcementsLeft = board.reinforcementsLeft(currentPlayerId);

    for (int r = 1; r <= reinforcementsLeft; r++) {
      int finalR = r;
      actions.addAll(board.getTerritories().entrySet().stream()
          .filter(t -> t.getValue().getOccupantPlayerId() == currentPlayerId)
          .map(t -> RiskAction.reinforce(t.getKey(), finalR)).collect(Collectors.toSet()));
    }
    return actions;
  }

  private Set<RiskAction> attackGPA() {
    Set<RiskAction> actions = new HashSet<>();
    actions.add(RiskAction.endPhase());

    Set<Integer> territories = board
        .occupiedTerritoriesByPlayerWithMoreThan1Troops(this.currentPlayerId);
    for (Integer territory : territories) {
      Set<Integer> neighbors = board.neighboringEnemyTerritories(territory);
      int maxAttack = board.getMaxAttackingTroops(territory);
      for (int t = 1; t <= maxAttack; t++) {
        final int finalT = t;
        actions.addAll(
            neighbors.stream().map(n -> RiskAction.attack(territory, n, finalT))
                .collect(Collectors.toSet()));
      }
    }

    return actions;
  }

  private Set<RiskAction> casualtiesGPA() {
    return possibleCasualties(board.getNrOfAttackerDice(), board.getNrOfDefenderDice());
  }

  private static Set<RiskAction> possibleCasualties(final int attackerDice,
      final int defenderDice) {
    final int dice = Math.min(attackerDice, defenderDice);
    return IntStream.rangeClosed(0, dice)
        .mapToObj(die -> RiskAction.casualties(die, dice - die)).collect(Collectors.toSet());
  }

  @Override
  public RiskBoard getBoard() {
    return new RiskBoard(board);
  }

  @Override
  public boolean isValidAction(RiskAction riskAction) {
    if (currentPlayerId < 0) {
      int armiesFought = Math.min(board.getNrOfAttackerDice(), board.getNrOfDefenderDice());
      int attackerCasualties = riskAction.attackerCasualties();
      int defenderCasualties = riskAction.defenderCasualties();
      return attackerCasualties + defenderCasualties == armiesFought;
    } else if (isInitialSelect()) {
      int selected = riskAction.selected();
      return board.isTerritory(selected) && !(
          0 <= board.getTerritoryOccupantId(selected)
              && board.getTerritoryOccupantId(selected) < getNumberOfPlayers());
    } else if (board.areReinforcementsLeft()) {
      return board.isTerritory(riskAction.reinforcedId()) && riskAction.troops() == 1
          && board.getTerritoryOccupantId(riskAction.reinforcedId()) == currentPlayerId;
    } else if (board.isReinforcementPhase()) {
      int reinforcementsLeft = board.reinforcementsLeft(currentPlayerId);
      int reinforced = riskAction.reinforcedId();
      return 1 <= riskAction.troops() && riskAction.troops() <= reinforcementsLeft && board
          .isTerritory(reinforced) && board.getTerritoryOccupantId(reinforced) == currentPlayerId;
    } else if (board.isAttackPhase()) {
      int attackingId = riskAction.attackingId();
      int defendingId = riskAction.defendingId();
      int troops = riskAction.troops();

      return board.getTerritoryOccupantId(attackingId) == this.currentPlayerId
          && 0 < troops
          && troops <= board.getMaxAttackingTroops(attackingId)
          && troops < board.getTerritoryTroops(attackingId)
          && board.areNeighbors(attackingId, defendingId);

    }
    return false;
  }

  @Override
  public Game<RiskAction, RiskBoard> doAction(RiskAction riskAction) {
    Risk next = null;
    if (currentPlayerId < 0) {
      if (board.isAttack()) {

      }
    } else if (isInitialSelect()) {
      next = initialSelectDA(riskAction);
    } else if (isInitialReinforce()) {
      next = initialReinforceDA(riskAction);
    } else if (board.isReinforcementPhase()) {
      next = reinforceDA(riskAction);
    } else if (board.isAttackPhase()) {
      next = attackDA(riskAction);
    } else if (board.isOccupyPhase()) {

    } else if (board.isFortifyPhase()) {

    }

    if (next != null) {
      next.actionRecords.add(new ActionRecord<>(this.currentPlayerId, riskAction));
    }
    return next;
  }

  private Risk initialSelectDA(RiskAction riskAction) {
    int selected = riskAction.selected();

    if (!board.isTerritory(selected)) {
      throw new IllegalArgumentException(
          "Specified territoryId is not assigned a territory, could therefore not select");
    }

    if (0 <= board.getTerritoryOccupantId(selected)
        && board.getTerritoryOccupantId(selected) < getNumberOfPlayers()) {
      throw new IllegalArgumentException(
          "Specified territoryId has already an occupant, could therefore not select");
    }

    Risk next = new Risk(this);
    next.board.initialSelect(selected, next.currentPlayerId);
    next.currentPlayerId =
        (next.currentPlayerId + (getNumberOfPlayers() - 1)) % getNumberOfPlayers();

    if (!next.isInitialSelect()) {
      if (next.isInitialReinforce()) {
        next.currentPlayerId = 0;
      } else {
        next.currentPlayerId = 1;
        next.board.endMove(1);
      }
    }

    return next;
  }

  private Risk initialReinforceDA(RiskAction riskAction) {
    int reinforcedId = riskAction.reinforcedId();
    int troops = riskAction.troops();
    {
      String errorMsg = "";

      if (!board.isTerritory(reinforcedId)) {
        errorMsg = "Reinforced territory is not an assigned territoryId";
      } else if (board.getTerritoryOccupantId(reinforcedId) != currentPlayerId) {
        errorMsg = "Reinforced territory is not occupied by currentPlayer";
      }

      if (troops != 1) {
        if (!errorMsg.isEmpty()) {
          errorMsg = errorMsg.concat(", ");
        }
        errorMsg = errorMsg.concat(troops + " is an illegal number of troops");
      }

      if (!errorMsg.isEmpty()) {
        throw new IllegalArgumentException(errorMsg.concat(", could therefore not reinforce"));
      }
    }
    Risk next = new Risk(this);
    next.board.reinforce(currentPlayerId, reinforcedId, troops);

    if (next.isInitialReinforce()) {
      do {
        next.currentPlayerId =
            (next.currentPlayerId + (getNumberOfPlayers() - 1)) % getNumberOfPlayers();
      } while (next.board.reinforcementsLeft(next.currentPlayerId) <= 0);
    } else {
      next.currentPlayerId = 1;
      next.board.endMove(1);
    }

    return next;
  }

  private int nextPlayerId(int player) {
    player++;
    for (int n = 1; n < getNumberOfPlayers(); n++, player = (player + 1) % getNumberOfPlayers()) {
      for (Integer id : board.getTerritoryIds()) {
        if (board.getTerritoryOccupantId(id) == player) {
          return player;
        }
      }
    }

    return player;
  }

  private Risk reinforceDA(RiskAction riskAction) {
    int reinforcedId = riskAction.reinforcedId();
    int troops = riskAction.troops();
    {
      String errorMsg = "";
      if (!board.isTerritory(reinforcedId)) {
        errorMsg = "Reinforced territory is not an assigned territoryId";
      } else if (board.getTerritoryOccupantId(reinforcedId) != currentPlayerId) {
        errorMsg = "Reinforced territory is not occupied by currentPlayer";
      }
      if (!(1 <= troops && troops <= board.reinforcementsLeft(currentPlayerId))) {
        if (!errorMsg.isEmpty()) {
          errorMsg = errorMsg.concat(", ");
        }
        errorMsg = errorMsg.concat(troops + " is an illegal number of troops");
      }

      if (!errorMsg.isEmpty()) {
        throw new IllegalArgumentException(errorMsg.concat(", could therefore not reinforce"));
      }
    }

    Risk next = new Risk(this);

    next.board.reinforce(next.currentPlayerId, reinforcedId, troops);
    if (next.board.reinforcementsLeft(currentPlayerId) == 0) {
      next.board.endReinforcementPhase();
    }

    return next;

  }

  private Risk attackDA(RiskAction riskAction) {
    int attackingId = riskAction.attackingId();
    int defendingId = riskAction.defendingId();
    int troops = riskAction.troops();

    {
      String errorMsg = "";
      if (board.getTerritoryOccupantId(attackingId) != this.currentPlayerId) {
        errorMsg = "Attacking territory does not belong to currentPlayer";
      } else if (!(0 < troops
          && troops <= board.getMaxAttackingTroops(attackingId)
          && troops < board.getTerritoryTroops(attackingId))) {
        errorMsg = "Illegal number of troops";
      }
      if (!board.areNeighbors(attackingId, defendingId)) {
        if (!errorMsg.isEmpty()) {
          errorMsg = errorMsg.concat(", ");
        }
        errorMsg = errorMsg
            .concat("Attacking and defending territory are not neighboring territories");
      }

      if (!errorMsg.isEmpty()) {
        throw new IllegalArgumentException(errorMsg.concat(", could therefore not attack"));
      }
    }

    Risk next = new Risk(this);
    next.currentPlayerId = -6;

    next.board.startAttack(attackingId, defendingId, troops);

    return next;
  }

  @Override
  public RiskAction determineNextAction() {
    if (currentPlayerId >= 0) {
      return null;
    }

    if (board.isAttack()) {
      return calculateCasualties();
    }

    return null;
  }

  private RiskAction calculateCasualties() {
    int attacker = board.getNrOfAttackerDice();
    int defender = board.getNrOfDefenderDice();

    int compareDice = Math.min(attacker, defender);
    attackerDice.rollN(attacker);
    defenderDice.rollN(defender);

    attackerDice.sortReverse();
    defenderDice.sortReverse();

    attacker = 0;
    defender = 0;

    for (int die = 0; die < compareDice; die++) {
      if (attackerDice.getFaceOf(die) > defenderDice.getFaceOf(die)) {
        defender++;
      } else {
        attacker++;
      }
    }

    return RiskAction.casualties(attacker, defender);
  }

  @Override
  public List<ActionRecord<RiskAction>> getActionRecords() {
    return this.actionRecords;
  }

  @Override
  public boolean isCanonical() {
    return this.canonical;
  }

  @Override
  public Game<RiskAction, RiskBoard> getGame(int p) {
    if (!canonical) {
      return new Risk(this);
    }
    return stripOutUnknownInformation(new Risk(this), p);
  }

  @Override
  public String toString() {
    return "Risk: " + currentPlayerId + ", " + Arrays.toString(getGameUtilityValue());
  }

  @Override
  public String toTextRepresentation() {
    StringBuilder map = new StringBuilder(board.getMap());
    final Map<Integer, RiskTerritory> territories = board.getTerritories();

    for (Integer i : territories.keySet()) {
      String target = "[" + i + "]";
      int occupantPlayerId = territories.get(i).getOccupantPlayerId();
      int troops = territories.get(i).getTroops();
      String replacement = String.format("%d:%d", occupantPlayerId, troops);
      int pre = map.indexOf(target);
      int post = pre + (target.length() - 1);

      int toCutWhiteSpace = replacement.length() - 1;

      while (toCutWhiteSpace != 0) {
        int cutFrom;
        if (toCutWhiteSpace % 2 == 0) {
          cutFrom = pre;
        } else {
          cutFrom = post;
        }
        char c;
        do {
          cutFrom += (toCutWhiteSpace % 2) == 0 ? -1 : 1;
          c = map.charAt(cutFrom);
        } while (!(('0' < c && c <= '9') || ('a' <= c && c < 'z')));

        if (toCutWhiteSpace > 0) {
          c = decreaseLexicographical(c);
        } else {
          c = increaseLexicographical(c);
        }
        map.setCharAt(cutFrom, c);

        toCutWhiteSpace += toCutWhiteSpace < 0 ? 1 : -1; // approach 0 step by step
      }

      map = map.replace(pre, post + 1, "[" + replacement + "]");
    }

    {
      int i = 0;
      boolean consume = true;
      while (i < map.length()) {
        char c = map.charAt(i);
        if (consume) {
          if (c == '[') {
            consume = false;
            map.deleteCharAt(i);
          } else if (('0' <= c && c <= '9') || ('a' <= c && c <= 'z')) {
            String whitespace = whitespace(c);
            map.replace(i, i + 1, whitespace);
            i += whitespace.length() - 1;
          }
        } else if (c == ']') {
          consume = true;
          map.deleteCharAt(i);
          i--;
        }
        i++;
      }
    }

    return map.toString();
  }

  private static char decreaseLexicographical(char c) {
    if (c == '0') {
      return c;
    }
    if (c == 'a') {
      return '9';
    }

    return --c;
  }

  private static char increaseLexicographical(char c) {
    if (c == 'z') {
      return 'z';
    }
    if (c == '9') {
      return 'a';
    }

    return ++c;
  }

  private static String whitespace(char c) {
    if ('a' <= c) {
      return whitespace((c - 'a') + 10);
    }
    return whitespace(c - '0');
  }

  private static String whitespace(int n) {
    String ret = "";
    for (int i = 0; i < n; i++) {
      ret = ret.concat(" ");
    }

    return ret;
  }

  private static Risk stripOutUnknownInformation(Risk game) {
    return game;
  }

  private static Risk stripOutUnknownInformation(Risk game, int player) {
    Risk next = stripOutUnknownInformation(game);
    return next;
  }

}
