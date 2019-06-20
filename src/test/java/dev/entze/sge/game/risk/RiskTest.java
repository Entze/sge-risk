package dev.entze.sge.game.risk;

import static org.junit.Assert.assertEquals;

import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class RiskTest {

  private final String simpleConfigYaml =
      "!!dev.entze.sge.game.risk.configuration.RiskConfiguration\n"
          + "cardTypesWithoutJoker: 3\n"
          + "chooseInitialTerritories: false\n"
          + "continents: !!set\n"
          + "  ? {continentId: 0, troopBonus: 1}\n"
          + "  : null\n"
          + "fortifyOnlyFromSingleTerritory: true\n"
          + "fortifyOnlyWithNonFightingArmies: false\n"
          + "initialTroops: [3]\n"
          + "map: |-\n"
          + "  +-----+\n"
          + "  |2[0]2|\n"
          + "  +-----+\n"
          + "  7\\5+-----+\n"
          + "  8\\____|2[1]2|\n"
          + "  8/4+-----+\n"
          + "  7/\n"
          + "  +-----+\n"
          + "  |2[2]2|\n"
          + "  +-----+\n"
          + "maxAttackerDice: 3\n"
          + "maxDefenderDice: 2\n"
          + "maxNumberOfPlayers: 2\n"
          + "missions: !!set {}\n"
          + "numberOfJokers: 2\n"
          + "occupyOnlyWithAttackingArmies: false\n"
          + "reinforcementAtLeast: 3\n"
          + "reinforcementThreshold: 3\n"
          + "territories: !!set\n"
          + "  ? cardType: 1\n"
          + "    connects: !!set {0: null, 2: null}\n"
          + "    territoryId: 1\n"
          + "  : null\n"
          + "  ? cardType: 0\n"
          + "    connects: !!set {2: null, 1: null}\n"
          + "    territoryId: 0\n"
          + "  : null\n"
          + "  ? cardType: 2\n"
          + "    connects: !!set {1: null, 0: null}\n"
          + "    territoryId: 2\n"
          + "  : null\n"
          + "withCards: true\n"
          + "withMissions: true";


  @Test
  public void test_yaml_dump_0() {
    Yaml yaml = RiskConfiguration.getYaml();

    RiskConfiguration riskConfiguration = RiskConfiguration.RISK_DEFAULT_CONFIG;// = new configuration();

    System.out.println(yaml.dump(riskConfiguration));

  }

  @Test
  public void test_yaml_dump_1() {
    Yaml yaml = RiskConfiguration.getYaml();

    RiskConfiguration riskConfiguration = new RiskConfiguration();

    riskConfiguration.setCardTypesWithoutJoker(3);
    riskConfiguration.setChooseInitialTerritories(false);
    riskConfiguration.setContinents(Set.of(new RiskContinentConfiguration(0, 1)));
    Set<RiskTerritoryConfiguration> territories = new HashSet<>();
    IntStream.range(0, 3).forEach(i -> {
      RiskTerritoryConfiguration territory = new RiskTerritoryConfiguration(i, i, 0);
      territory.setConnects(Set.of((i + 2) % 3, (i + 1) % 3));
      territories.add(territory);
    });
    riskConfiguration.setTerritories(territories);
    riskConfiguration.setInitialTroops(3);
    riskConfiguration.setMap("+-----+\n"
        + "|2[0]2|\n"
        + "+-----+\n"
        + "7\\5+-----+\n"
        + "8\\____|2[1]2|\n"
        + "8/4+-----+\n"
        + "7/\n"
        + "+-----+\n"
        + "|2[2]2|\n"
        + "+-----+");

    System.out.println(yaml.dump(riskConfiguration));

  }

  @Test
  public void test_game_creation_1() {
    Risk risk = new Risk(simpleConfigYaml, 2);
    RiskBoard board = risk.getBoard();
    assertEquals(2, risk.getNumberOfPlayers());
    assertEquals(2, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test
  public void test_game_creation_2() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 2);
    RiskBoard board = risk.getBoard();
    assertEquals(2, risk.getNumberOfPlayers());
    assertEquals(2, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test
  public void test_game_doAction() {

  }

}
