package dev.entze.sge.game.risk;

import dev.entze.sge.game.ActionRecord;
import dev.entze.sge.game.Game;
import dev.entze.sge.game.risk.board.RiskBoard;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Risk implements Game<RiskAction, RiskBoard> {

  private int currentPlayerId;
  private final boolean canonical;
  private List<ActionRecord<RiskAction>> actionRecords;
  private RiskBoard board;

  private final int numberOfPlayers;

  private final String map;

  /*
  public Risk(String yamlString) {
    Yaml yaml = new Yaml();
    Map<String, Object> config = yaml.load(yamlString);

    int maxNumberOfPlayers = getMaximumNumberOfPlayers();
    int numberOfJokers = 2;
    int cardTypes = 3;
    List<Integer[]> continents;
  }*/

  public Risk(Risk risk) {
    this(risk.currentPlayerId, risk.canonical, risk.actionRecords, risk.board,
        risk.numberOfPlayers, risk.map);
  }

  public Risk(int currentPlayerId, boolean canonical,
      List<ActionRecord<RiskAction>> actionRecords, RiskBoard board, int numberOfPlayers,
      String map) {
    this.currentPlayerId = currentPlayerId;
    this.canonical = canonical;
    this.actionRecords = actionRecords;
    this.board = board;
    this.numberOfPlayers = numberOfPlayers;
    this.map = map;
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
    return 0;
  }

  @Override
  public int getCurrentPlayer() {
    return 0;
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
    return null;
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
}
