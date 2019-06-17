package dev.entze.sge.game.risk;

import dev.entze.sge.game.ActionRecord;
import dev.entze.sge.game.Game;
import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.board.RiskTerritory;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Risk implements Game<RiskAction, RiskBoard> {

  private final boolean canonical;
  private int currentPlayerId;
  private List<ActionRecord<RiskAction>> actionRecords;
  private RiskBoard board;


  public Risk(String yaml, int numberOfPlayers) {
    this((RiskConfiguration) RiskConfiguration.getYaml().load(yaml), numberOfPlayers);
  }

  public Risk(RiskConfiguration configuration, int numberOfPlayers) {
    this(0, true, Collections.emptyList(), new RiskBoard(configuration, numberOfPlayers));
  }

  public Risk(Risk risk) {
    this(risk.currentPlayerId, risk.canonical, risk.actionRecords, risk.board);
  }

  public Risk(int currentPlayerId, boolean canonical,
      List<ActionRecord<RiskAction>> actionRecords, RiskBoard board) {
    this.currentPlayerId = currentPlayerId;
    this.canonical = canonical;
    this.actionRecords = new ArrayList<>(actionRecords);
    this.board = board;
  }

  @Override
  public boolean isGameOver() {
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
    return null;
  }

  @Override
  public RiskBoard getBoard() {
    return new RiskBoard(board);
  }

  @Override
  public boolean isValidAction(RiskAction riskAction) {
    return false;
  }

  @Override
  public Game<RiskAction, RiskBoard> doAction(RiskAction riskAction) {
    return null;
  }

  @Override
  public RiskAction determineNextAction() {
    return null;
  }

  @Override
  public List<ActionRecord<RiskAction>> getActionRecords() {
    return null;
  }

  @Override
  public boolean isCanonical() {
    return false;
  }

  @Override
  public Game<RiskAction, RiskBoard> getGame(int i) {
    return null;
  }

  @Override
  public String toString() {
    return "Risk: " + currentPlayerId + ", " + Arrays.toString(getGameUtilityValue());
  }

  private static final String territoryInfoRegex = "\\[(?<id>[0-9]+)\\]";
  private static final Pattern territoryInfoPattern = Pattern.compile(territoryInfoRegex);

  @Override
  public String toTextRepresentation() {
    String map = board.getMap();
    final Map<Integer, RiskTerritory> territories = board.getTerritories();
    for (Integer i : territories.keySet()) {
      String target = "[" + i + "]";
      String replacement = String
          .format("%d:%d", territories.get(i).getOccupantPlayerId(),
              territories.get(i).getTroops());
      int pre = map.indexOf(target);
      int post = pre + (target.length() - 1);

      int toCutWhiteSpace = replacement.length() - target.length();

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

        //TODO: INCREASE or DECREASE depending on context
        c = decreaseLexicographical(c);
        map = String.format("%s%c%s", map.substring(0, cutFrom), c, map.substring(cutFrom + 1));

        toCutWhiteSpace += toCutWhiteSpace < 0 ? 1 : -1; // approach 0 step by step
      }

      map = map.replace(target, "[" + replacement + "]");

    }

    /*
    //TODO: only replace numbers which are not part of the score
    StringBuilder whitespace = new StringBuilder();
    for (int i = 0; i < ('z' - 'a' + 10); i++) {
      whitespace.append(' ');
    }

    for (char c = 'z'; c >= '1';
        c = decreaseLexicographical(c), whitespace.substring(0, whitespace.length() - 1)) {
      map = map.replaceAll("" + c, whitespace.toString());
    }
    map = map.replaceAll("0", "");
*/
    return map;
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

}
