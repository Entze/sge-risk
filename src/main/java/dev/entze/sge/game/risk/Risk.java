package dev.entze.sge.game.risk;

import dev.entze.sge.game.ActionRecord;
import dev.entze.sge.game.Game;
import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
}
