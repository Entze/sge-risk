package dev.entze.sge.game.risk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import dev.entze.sge.game.risk.board.RiskBoard;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
  public void test_game_creation_3() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 3);
    RiskBoard board = risk.getBoard();
    assertEquals(3, risk.getNumberOfPlayers());
    assertEquals(3, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test
  public void test_game_creation_4() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 4);
    RiskBoard board = risk.getBoard();
    assertEquals(4, risk.getNumberOfPlayers());
    assertEquals(4, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test
  public void test_game_creation_5() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 5);
    RiskBoard board = risk.getBoard();
    assertEquals(5, risk.getNumberOfPlayers());
    assertEquals(5, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test
  public void test_game_creation_6() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 6);
    RiskBoard board = risk.getBoard();
    assertEquals(6, risk.getNumberOfPlayers());
    assertEquals(6, board.getNumberOfPlayers());
    System.out.println(risk.toTextRepresentation());
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_game_creation_err_1() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 1);
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_game_creation_err_2() {
    Risk risk = new Risk(RiskConfiguration.RISK_DEFAULT_CONFIG, 7);
    fail();
  }

  @Test
  public void test_game_doAction_initialReinforce_1() {
    Risk risk = new Risk(simpleConfigYaml, 2);
    assertEquals(0, risk.getCurrentPlayer());
  }

  @Test
  public void test_game_doAction_initialSelect_1() {

    RiskConfiguration riskConfiguration = RiskConfiguration.getYaml().load(simpleConfigYaml);
    riskConfiguration.setChooseInitialTerritories(true);

    Risk risk = new Risk(riskConfiguration, 2);

    assertEquals(Set.of(RiskAction.select(0), RiskAction.select(1), RiskAction.select(2)),
        risk.getPossibleActions());

    assertEquals(0, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(0));

    assertEquals(1, risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));
    assertEquals(-1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(0, risk.getBoard().getTerritoryTroops(1));
    assertEquals(-1, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(0, risk.getBoard().getTerritoryTroops(2));

    assertEquals(Set.of(RiskAction.select(1), RiskAction.select(2)),
        risk.getPossibleActions());

    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));
    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(1, risk.getBoard().getTerritoryTroops(1));
    assertEquals(-1, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(0, risk.getBoard().getTerritoryTroops(2));

    assertEquals(Set.of(RiskAction.select(2)),
        risk.getPossibleActions());

    assertEquals(0, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(2));
    assertEquals(0, risk.getCurrentPlayer());

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));
    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(1, risk.getBoard().getTerritoryTroops(1));
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

  }

  @Test
  public void test_game_doAction_initialSelect_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setMaxNumberOfPlayers(3);
    config.setInitialTroops(1);
    config.setChooseInitialTerritories(true);

    Risk risk = new Risk(config, 3);

    assertEquals(0, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(0));
    assertEquals(2, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(1));
    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.select(2));
    assertEquals(1, risk.getCurrentPlayer());

  }

  @Test(expected = IllegalArgumentException.class)
  public void test_game_doAction_initialSelect_err_selecting_already_selected() {

    RiskConfiguration riskConfiguration = RiskConfiguration.getYaml().load(simpleConfigYaml);
    riskConfiguration.setChooseInitialTerritories(true);

    Risk risk = new Risk(riskConfiguration, 2);

    assertEquals(0, risk.getCurrentPlayer());

    assertEquals(Set.of(RiskAction.select(0), RiskAction.select(1), RiskAction.select(2)),
        risk.getPossibleActions());

    risk = (Risk) risk.doAction(RiskAction.select(0));

    assertEquals(Set.of(RiskAction.select(1), RiskAction.select(2)),
        risk.getPossibleActions());

    assertFalse(risk.isValidAction(RiskAction.select(0)));

    risk.doAction(RiskAction.select(0));
    fail();

  }

  @Test
  public void test_game_doAction_reinforce_1() {
    RiskConfiguration riskConfiguration = RiskConfiguration.getYaml().load(simpleConfigYaml);
    riskConfiguration.setChooseInitialTerritories(true);
    Risk risk = new Risk(riskConfiguration, 2);
    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(3, risk.getPossibleActions().size());
  }

  @Test
  public void test_game_doAction_reinforce_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    assertEquals(3, risk.getPossibleActions().size());
    assertEquals(IntStream.range(1, 4).mapToObj(r -> RiskAction.reinforce(1, r)).collect(
        Collectors.toSet()), risk.getPossibleActions());

    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(2, risk.getPossibleActions().size());
    assertEquals(IntStream.range(1, 3).mapToObj(r -> RiskAction.reinforce(1, r)).collect(
        Collectors.toSet()), risk.getPossibleActions());

    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 2));
    assertEquals(1, risk.getCurrentPlayer());

  }

  @Test
  public void test_game_doAction_reinforce_err_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));

    assertFalse(risk.isValidAction(RiskAction.reinforce(0, 3)));
    try {
      risk.doAction(RiskAction.reinforce(0, 3));
      fail();
    } catch (IllegalArgumentException ignored) {
    }

  }

  @Test
  public void test_game_doAction_reinforce_err_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));

    assertFalse(risk.isValidAction(RiskAction.reinforce(1, 0)));
    try {
      risk.doAction(RiskAction.reinforce(1, 0));
      fail();
    } catch (IllegalArgumentException ignored) {

    }
  }

  @Test
  public void test_game_doAction_reinforce_err_3() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));

    assertFalse(risk.isValidAction(RiskAction.reinforce(1, 999)));
    try {
      risk.doAction(RiskAction.reinforce(1, 999));
      fail();
    } catch (IllegalArgumentException ignored) {

    }

  }

  @Test
  public void test_game_doAction_attack_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));

    Set<RiskAction> expected = Stream.concat(
        IntStream.range(1, 4).mapToObj(t -> RiskAction.attack(1, 0, t)),
        IntStream.range(1, 4).mapToObj(t -> RiskAction.attack(1, 2, t))
    ).collect(Collectors.toSet());

    expected.add(RiskAction.endPhase());

    assertEquals(expected, risk.getPossibleActions());

    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));
    assertTrue(0 > risk.getCurrentPlayer());


  }

  @Test
  public void test_game_doAction_attack_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    config.setOccupyOnlyWithAttackingArmies(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));

    Set<RiskAction> expected = Stream.concat(
        IntStream.range(1, 6).mapToObj(t -> RiskAction.attack(1, 0, t)),
        IntStream.range(1, 6).mapToObj(t -> RiskAction.attack(1, 2, t))
    ).collect(Collectors.toSet());

    expected.add(RiskAction.endPhase());

    assertEquals(expected, risk.getPossibleActions());

    assertEquals(1, risk.getCurrentPlayer());
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 5));
    assertTrue(0 > risk.getCurrentPlayer());


  }

  @Test
  public void test_game_doAction_diceThrow_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));

    assertTrue(0 > risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(6, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(2, risk.getBoard().getTerritoryTroops(0));

    assertEquals(Set.of(RiskAction.casualties(0, 2),
        RiskAction.casualties(1, 1),
        RiskAction.casualties(2, 0)), risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.casualties(0, 2)));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));

    assertEquals(1, risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(6, risk.getBoard().getTerritoryTroops(1));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(0, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_diceThrow_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));

    assertTrue(0 > risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(6, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(2, risk.getBoard().getTerritoryTroops(0));

    assertEquals(Set.of(RiskAction.casualties(0, 2),
        RiskAction.casualties(1, 1),
        RiskAction.casualties(2, 0)), risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.casualties(1, 1)));
    risk = (Risk) risk.doAction(RiskAction.casualties(1, 1));

    assertEquals(1, risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(5, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_diceThrow_3() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    config.setOccupyOnlyWithAttackingArmies(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 5));

    assertTrue(0 > risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(6, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(2, risk.getBoard().getTerritoryTroops(0));

    assertEquals(Set.of(RiskAction.casualties(0, 2),
        RiskAction.casualties(1, 1),
        RiskAction.casualties(2, 0)), risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.casualties(1, 1)));
    risk = (Risk) risk.doAction(RiskAction.casualties(1, 1));

    assertEquals(1, risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(5, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_diceThrow_4() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    config.setOccupyOnlyWithAttackingArmies(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 5));

    assertTrue(0 > risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(6, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(2, risk.getBoard().getTerritoryTroops(0));

    assertEquals(Set.of(RiskAction.casualties(0, 2),
        RiskAction.casualties(1, 1),
        RiskAction.casualties(2, 0)), risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.casualties(1, 1)));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));

    assertEquals(1, risk.getCurrentPlayer());
    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(1, risk.getBoard().getTerritoryTroops(1));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(5, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_occupy_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));

    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));

    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(Set.of(RiskAction.occupy(1), RiskAction.occupy(2), RiskAction.occupy(3),
        RiskAction.occupy(4), RiskAction.occupy(5)),
        risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.occupy(1)));
    risk = (Risk) risk.doAction(RiskAction.occupy(1));

    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(5, risk.getBoard().getTerritoryTroops(1));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(1, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_occupy_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    config.setOccupyOnlyWithAttackingArmies(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));

    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));

    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(0, risk.getBoard().getTerritoryOccupantId(2));
    assertEquals(1, risk.getBoard().getTerritoryTroops(2));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(1));
    assertEquals(3, risk.getBoard().getTerritoryTroops(1));

    assertEquals(1, risk.getBoard().getTerritoryOccupantId(0));
    assertEquals(3, risk.getBoard().getTerritoryTroops(0));

  }

  @Test
  public void test_game_doAction_endAttack_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 0, 3));

    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));

    risk = (Risk) risk.doAction(RiskAction.occupy(1));

    assertTrue(risk.getPossibleActions().contains(RiskAction.endPhase()));
    assertTrue(risk.isValidAction(RiskAction.endPhase()));

    risk = (Risk) risk.doAction(RiskAction.endPhase());

    assertEquals(1, risk.getCurrentPlayer());

  }


  @Test
  public void test_game_getGame_independent() {
    Risk risk = new Risk(simpleConfigYaml, 2);

    Risk other = (Risk) risk.getGame();

    RiskBoard board = risk.getBoard();
    RiskBoard otherBoard = other.getBoard();

    otherBoard.getTerritories().values().forEach(t -> {
      t.setOccupantPlayerId(0);
      t.setTroops(1000);
    });

    assertFalse(board.getTerritories().values().stream()
        .anyMatch(t -> t.getOccupantPlayerId() == 0 && t.getTroops() == 1000));

  }

}
