package dev.entze.sge.game.risk;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class RiskBoard {

  private final Graph<RiskTerritory, DefaultEdge> gameBoard;
  private final Deque<RiskCard> deckOfCards;

  private final RiskCard[][] playerCards;

  private final RiskContinent[] continents;

  private final int[] nonDeployedReinforcements;

//  public RiskBoard(int numberOfPlayers, Map<Integer, Set<Integer>> territories, List<Integer[]> continents)

  public RiskBoard(int numberOfPlayers, Graph<RiskTerritory, DefaultEdge> gameBoard,
      Collection<RiskCard> deckOfCards, RiskContinent[] continents) {
    this.gameBoard = gameBoard;
    this.deckOfCards = new ArrayDeque<>(deckOfCards);
    this.playerCards = new RiskCard[numberOfPlayers][6];
    this.continents = continents;
    this.nonDeployedReinforcements = new int[numberOfPlayers];
  }


}
