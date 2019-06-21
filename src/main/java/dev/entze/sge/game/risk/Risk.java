package dev.entze.sge.game.risk;

import dev.entze.sge.game.ActionRecord;
import dev.entze.sge.game.Dice;
import dev.entze.sge.game.Game;
import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.board.RiskTerritory;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
    if (isInitialSelect()) {
      return initialSelectGPA();
    } else if (currentPlayerId < 0) {

    } else if (board.isReinforcementPhase()) {
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

  private Set<RiskAction> initialSelectGPA() {
    return board.getTerritories().entrySet().stream().filter(
        t -> !(0 <= t.getValue().getOccupantPlayerId()
            && t.getValue().getOccupantPlayerId() < getNumberOfPlayers())).mapToInt(Entry::getKey)
        .mapToObj(RiskAction::select).collect(Collectors.toSet());
  }

  @Override
  public RiskBoard getBoard() {
    return new RiskBoard(board);
  }

  @Override
  public boolean isValidAction(RiskAction riskAction) {
    if (isInitialSelect()) {
      int selected = riskAction.selected();
      return board.getTerritoryIds().contains(selected) && !(
          0 <= board.getTerritoryOccupantId(selected)
              && board.getTerritoryOccupantId(selected) < getNumberOfPlayers());
    }
    return false;
  }

  @Override
  public Game<RiskAction, RiskBoard> doAction(RiskAction riskAction) {
    Risk next = null;
    if (isInitialSelect()) {
      next = initialSelectDA(riskAction);
    } else if (currentPlayerId < 0) {

    } else if (board.isReinforcementPhase()) {

    } else if (board.isAttackPhase()) {

    } else if (board.isOccupyPhase()) {

    } else if (board.isBolsterPhase()) {

    }

    return next;
  }

  private Risk initialSelectDA(RiskAction riskAction) {
    int selected = riskAction.selected();

    if (!board.getTerritoryIds().contains(selected)) {
      throw new IllegalArgumentException(
          "Specified territoryId is not assigned a territory. Could therefore not select");
    }

    if (0 <= board.getTerritoryOccupantId(selected)
        && board.getTerritoryOccupantId(selected) < getNumberOfPlayers()) {
      throw new IllegalArgumentException(
          "Specified territoryId has already an occupant. Could therefore not select");
    }

    Risk next = new Risk(this);
    next.board.initialSelect(selected, next.currentPlayerId);
    next.currentPlayerId =
        (next.currentPlayerId + (getNumberOfPlayers() - 1)) % getNumberOfPlayers();

    if (!next.isInitialSelect()) {
      next.currentPlayerId = 1;
      next.board.endMove(next.currentPlayerId);
    }

    return next;
  }

  @Override
  public RiskAction determineNextAction() {
    if (currentPlayerId >= 0) {
      return null;
    }

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
    return stripOutUnknownInformation(new Risk(currentPlayerId, false, actionRecords, board), p);
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
