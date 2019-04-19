package dev.entze.sge.game.risk;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class RiskBoard {

  private final Graph<RiskTerritory, DefaultEdge> gameBoard;
  private final Deque<RiskCard> deckOfCards;
  private final RiskCard[][] playerCards;

  private final RiskContinent[] continents;

  private final int[] nonDeployedReinforcements;

  public RiskBoard(int numberOfPlayers, String initialBoard) {
    this(numberOfPlayers, initialBoard.split("\n"));
  }

  public RiskBoard(int numberOfPlayers, String[] lines) {
    this(numberOfPlayers, getGameBoardFromLines(lines), getDeckOfCardsFromLines(lines), getContinentsFromLines(lines));
  }

  public RiskBoard(int numberOfPlayers, Graph<RiskTerritory, DefaultEdge> gameBoard,
      Collection<RiskCard> deckOfCards, RiskContinent[] continents) {
    this.gameBoard = gameBoard;
    this.deckOfCards = new ArrayDeque<>(deckOfCards);
    this.playerCards = new RiskCard[numberOfPlayers][6];
    this.continents = continents;
    this.nonDeployedReinforcements = new int[numberOfPlayers];
  }


  public Graph<RiskTerritory, DefaultEdge> getGameBoard() {
    return gameBoard;
  }

  public Deque<RiskCard> getDeckOfCards() {
    return deckOfCards;
  }

  public RiskCard[][] getPlayerCards() {
    return playerCards;
  }

  public RiskContinent[] getContinents() {
    return continents;
  }

  public int[] getNonDeployedReinforcements() {
    return nonDeployedReinforcements;
  }

  public static Graph<RiskTerritory, DefaultEdge> getGameBoardFromLines(String[] lines) {
    Graph<RiskTerritory, DefaultEdge> gameBoard = new SimpleGraph<>(DefaultEdge.class);

    return gameBoard;
  }

  public static Set<RiskCard> getDeckOfCardsFromLines(String[] lines) {
    Set<RiskCard> deckOfCards = new HashSet<>();

    return deckOfCards;
  }

  public static RiskContinent[] getContinentsFromLines(String[] lines) {
    RiskContinent[] continents = null;
    return continents;
  }

}
