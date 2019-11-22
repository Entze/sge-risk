package at.ac.tuwien.ifs.sge.game.risk.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskConfiguration;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskContinentConfiguration;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskTerritoryConfiguration;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.risk.generators.RiskActionGenerator;
import java.lang.module.Configuration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

@RunWith(JUnitQuickcheck.class)
public class RiskTest {

  private final String simpleConfigYaml =
      "!!at.ac.tuwien.ifs.sge.game.risk.configuration.RiskConfiguration\n"
          + "cardTypesWithoutJoker: 2\n"
          + "chooseInitialTerritories: false\n"
          + "continents:\n"
          + "- {continentId: 0, troopBonus: 1}\n"
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
          + "maxExtraBonus: 1\n"
          + "maxNumberOfPlayers: 2\n"
          + "missions: []\n"
          + "numberOfJokers: 1\n"
          + "occupyOnlyWithAttackingArmies: false\n"
          + "reinforcementAtLeast: 3\n"
          + "reinforcementThreshold: 3\n"
          + "territories:\n"
          + "- cardType: 1\n"
          + "  connects: [0, 2]\n"
          + "  continentId: 0\n"
          + "  territoryId: 1\n"
          + "- cardType: 2\n"
          + "  connects: [0, 1]\n"
          + "  continentId: 0\n"
          + "  territoryId: 2\n"
          + "- cardType: 1\n"
          + "  connects: [1, 2]\n"
          + "  continentId: 0\n"
          + "  territoryId: 0\n"
          + "tradeInBonus: [1]\n"
          + "withCards: true\n"
          + "withMissions: false\n";

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
    riskConfiguration.setContinents(List.of(new RiskContinentConfiguration(0, 1)));
    Set<RiskTerritoryConfiguration> territories = new HashSet<>();
    IntStream.range(0, 3).forEach(i -> {
      RiskTerritoryConfiguration territory = new RiskTerritoryConfiguration(i, i + 1, 0);
      territory.setConnects(List.of((i + 2) % 3, (i + 1) % 3));
      territories.add(territory);
    });
    riskConfiguration.setTerritories(new ArrayList<>(territories));
    riskConfiguration.setInitialTroops(new int[]{3});
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
  public void test_yaml_load_0() {
    Yaml yaml = RiskConfiguration.getYaml();

    String config = yaml.dump(RiskConfiguration.RISK_DEFAULT_CONFIG);

    assertEquals(RiskConfiguration.RISK_DEFAULT_CONFIG, yaml.load(config));

  }

  @Test
  public void test_yaml_load_1() {
    Yaml yaml = RiskConfiguration.getYaml();

    RiskConfiguration riskConfiguration = yaml.load(simpleConfigYaml);

    assertEquals(simpleConfigYaml, yaml.dump(riskConfiguration));

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
    config.setInitialTroops(new int[]{1});
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

    assertEquals(1, risk.getPossibleActions().size());
    assertEquals(Set.of(RiskAction.reinforce(1, 3)), risk.getPossibleActions());
  }

  @Test
  public void test_game_doAction_cards_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    for (RiskTerritoryConfiguration territory : config.getTerritories()) {
      if (territory.getCardType() != 1) {
        territory.setCardType(1);
      }
    }
    config.setChooseInitialTerritories(true);
    config.setFortifyOnlyFromSingleTerritory(true);
    config.setFortifyOnlyWithNonFightingArmies(false);
    config.setCardTypesWithoutJoker(1);
    config.setNumberOfJokers(0);
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
    risk = (Risk) risk.doAction(RiskAction.occupy(2));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    int player = risk.getCurrentPlayer();
    int cards = risk.getBoard().getPlayerCards(player).size();
    risk = (Risk) risk.doAction(RiskAction.fortify(1, 0, 1));
    assertEquals(cards + 1, risk.getBoard().getPlayerCards(player).size());

    risk = (Risk) risk.doAction(RiskAction.reinforce(2, 3));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());

    assertEquals(Set.of(RiskAction.playCards(0)), risk.getPossibleActions());
    risk = (Risk) risk.doAction(RiskAction.playCards(0));
    assertTrue(
        risk.getPossibleActions().contains(RiskAction.bonusTroopsFromCards(0)) || risk
            .getPossibleActions()
            .contains(RiskAction.bonusTroopsFromCards(1)));
    risk = (Risk) risk.doAction(risk.determineNextAction());

    int reinforcementsLeft = risk.getBoard().reinforcementsLeft(risk.getCurrentPlayer());
    assertTrue("Was: " + reinforcementsLeft, 4 == reinforcementsLeft || reinforcementsLeft == 6);

    Set<RiskAction> expected = Stream.concat(
        IntStream.rangeClosed(2, 4).mapToObj(t -> RiskAction.reinforce(0, t)),
        IntStream.rangeClosed(2, 4).mapToObj(t -> RiskAction.reinforce(1, t)))
        .collect(Collectors.toCollection(HashSet::new));

    Set<RiskAction> actual = risk.getPossibleActions();

    assertTrue("Expected: " + expected + " Actual: " + actual, actual.containsAll(expected));

    if (reinforcementsLeft == 4) {
      assertEquals("Expected: " + expected + " Actual: " + actual, expected.size() + 2,
          actual.size());
    } else {
      assertEquals("Expected: " + expected + " Actual: " + actual, expected.size() + 3,
          actual.size());
    }

  }

  @Test
  public void test_game_doAction_cards_2() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    for (RiskTerritoryConfiguration territory : config.getTerritories()) {
      if (territory.getCardType() != 1) {
        territory.setCardType(1);
      }
    }
    config.setChooseInitialTerritories(true);
    config.setFortifyOnlyFromSingleTerritory(true);
    config.setFortifyOnlyWithNonFightingArmies(false);
    config.setCardTypesWithoutJoker(1);
    config.setNumberOfJokers(0);
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
    risk = (Risk) risk.doAction(RiskAction.occupy(2));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    int player = risk.getCurrentPlayer();
    int cards = risk.getBoard().getPlayerCards(player).size();
    risk = (Risk) risk.doAction(RiskAction.fortify(1, 0, 1));
    assertEquals(cards + 1, risk.getBoard().getPlayerCards(player).size());

    risk = (Risk) risk.doAction(RiskAction.reinforce(2, 3));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());

    Risk playerRisk = (Risk) risk.getGame(risk.getCurrentPlayer());

    assertEquals(Set.of(RiskAction.playCards(0)), playerRisk.getPossibleActions());
    playerRisk = (Risk) playerRisk.doAction(RiskAction.playCards(0));
    assertTrue(
        playerRisk.getPossibleActions().contains(RiskAction.bonusTroopsFromCards(0)) || playerRisk
            .getPossibleActions()
            .contains(RiskAction.bonusTroopsFromCards(1)));

    playerRisk.determineNextAction(); //TODO
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
  public void test_game_doAction_fortify_1() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    config.setFortifyOnlyFromSingleTerritory(true);
    config.setFortifyOnlyWithNonFightingArmies(false);
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

    risk = (Risk) risk.doAction(RiskAction.occupy(2));

    risk = (Risk) risk.doAction(RiskAction.endPhase());

    assertEquals(1, risk.getCurrentPlayer());

    assertEquals(
        Set.of(RiskAction.endPhase(), RiskAction.fortify(0, 1, 1),
            RiskAction.fortify(1, 0, 1),
            RiskAction.fortify(1, 0, 2),
            RiskAction.fortify(1, 0, 3)
        ),
        risk.getPossibleActions());

    assertTrue(risk.isValidAction(RiskAction.fortify(1, 0, 1)));
    risk = (Risk) risk.doAction(RiskAction.fortify(1, 0, 1));

    assertEquals(3, risk.getBoard().getTerritoryTroops(0));
    assertEquals(3, risk.getBoard().getTerritoryTroops(1));

    assertEquals(0, risk.getCurrentPlayer());

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

  @Test
  public void test_constructor_zero() {
    new Risk();
  }

  @Test
  public void test_constructor_one() {
    new Risk(2);
  }

  public void test_game_checkIfAllPossibleActionsAreValid() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    Deque<Risk> risks = new ArrayDeque<>();

    risks.add(risk);

    Set<RiskAction> possibleActions = new HashSet<>();
    while (!risks.isEmpty()) {

      risk = risks.removeFirst();
      possibleActions.addAll(risk.getPossibleActions());
      for (RiskAction possibleAction : possibleActions) {
        assertTrue(risk.getActionRecords().toString(), risk.isValidAction(possibleAction));
        try {
          risks.addFirst((Risk) risk.doAction(possibleAction));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
          System.err.println(risk.toTextRepresentation() + "\n" + possibleActions);
          fail(
              ActionRecord.iterableToString(risk.getActionRecords()) + "<" + risk.getCurrentPlayer()
                  + " " + possibleAction.toString() + ">");
        }
      }

      possibleActions.clear();
    }

  }

  public void test_game_checkIfThereIsAlwaysANextMove() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setChooseInitialTerritories(true);
    Risk risk = new Risk(config, 2);

    Deque<RiskAction> actions = new ArrayDeque<>();
    Deque<Risk> risks = new ArrayDeque<>();
    Deque<Integer> actionLength = new ArrayDeque<>();

    risks.addFirst(risk);

    Set<RiskAction> possibleActions = new HashSet<>();
    while (!risks.isEmpty()) {

      risk = risks.removeFirst();

      System.out.println(risk.getPreviousAction());

      assertEquals(ActionRecord.iterableToString(risk.getActionRecords()), risk.isGameOver(),
          risk.getPossibleActions().isEmpty());

      possibleActions.addAll(risk.getPossibleActions());
      for (RiskAction possibleAction : possibleActions) {
        risks.add((Risk) risk.doAction(possibleAction));
      }

      possibleActions.clear();
    }

  }

  @Property
  public void prop_action_toString_fromString_reverse(
      @From(RiskActionGenerator.class) RiskAction action) {

    RiskAction actual = RiskAction.fromString(action.toString());
    assertEquals("Expected: " + action.toString() + " Actual: " + actual, action,
        RiskAction.fromString(action.toString()));

  }

  public void test_specialcase() {
    String pliesString = "<0, [-(1)->16]> <1, [-(1)->9]> <0, [-(1)->11]> <1, [-(1)->20]> <0, [-(1)->40]> <1, [-(1)->7]> <0, [-(1)->38]> <1, [-(1)->23]> <0, [-(1)->28]> <1, [-(1)->18]> <0, [-(1)->19]> <1, [-(1)->2]> <0, [-(1)->0]> <1, [-(1)->14]> <0, [-(1)->1]> <1, [-(1)->31]> <0, [-(1)->30]> <1, [-(1)->21]> <0, [-(1)->13]> <1, [-(1)->37]> <0, [-(1)->34]> <1, [-(1)->17]> <0, [-(1)->24]> <1, [-(1)->27]> <0, [-(1)->15]> <1, [-(1)->41]> <0, [-(1)->35]> <1, [-(1)->5]> <0, [-(1)->4]> <1, [-(1)->12]> <0, [-(1)->8]> <1, [-(1)->25]> <0, [-(1)->10]> <1, [-(1)->36]> <0, [-(1)->39]> <1, [-(1)->32]> <0, [-(1)->26]> <1, [-(1)->22]> <0, [-(1)->6]> <1, [-(1)->3]> <0, [-(1)->29]> <1, [-(1)->33]> <0, [-(1)->6]> <1, [-(1)->12]> <0, [-(1)->13]> <1, [-(1)->37]> <0, [-(1)->15]> <1, [-(1)->9]> <0, [-(1)->11]> <1, [-(1)->2]> <0, [-(1)->8]> <1, [-(1)->25]> <0, [-(1)->16]> <1, [-(1)->21]> <0, [-(1)->16]> <1, [-(1)->41]> <0, [-(1)->19]> <1, [-(1)->12]> <0, [-(1)->28]> <1, [-(1)->36]> <0, [-(1)->16]> <1, [-(1)->9]> <0, [-(1)->30]> <1, [-(1)->17]> <0, [-(1)->0]> <1, [-(1)->7]> <0, [-(1)->34]> <1, [-(1)->33]> <0, [-(1)->26]> <1, [-(1)->25]> <0, [-(1)->13]> <1, [-(1)->2]> <0, [-(1)->34]> <1, [-(1)->31]> <0, [-(1)->0]> <1, [-(1)->36]> <0, [-(1)->35]> <1, [-(1)->12]> <0, [-(1)->13]> <1, [-(1)->33]> <0, [-(1)->28]> <1, [-(1)->36]> <0, [-(1)->38]> <1, [-(1)->21]> <0, [-(1)->16]> <1, [-(1)->12]> <0, [-(1)->34]> <1, [-(1)->33]> <0, [-(1)->40]> <1, [-(1)->23]> <0, [-(1)->35]> <1, [-(1)->21]> <0, [-(1)->19]> <1, [-(1)->17]> <0, [-(1)->0]> <1, [-(1)->7]> <0, [-(1)->19]> <1, [-(1)->17]> <0, [-(1)->24]> <1, [-(1)->25] [-(2)->23] [-(1)->33] [-(1)->27] [-(1)->5] [-(2)->20] [17-(3)->15]> < [0X2]> <1, [O2] [15-(1)->19]> < [1X0]> <1, [2-(1)->8]> < [1X0]> <1, [31-(1)->0]> < [0X1]> <1, [36-(2)->35]> < [1X1]> <1, [5-(1)->6]> < [1X0]> <1, [33-(3)->35]> < [2X0]> <1, [33-(1)->29]> < [1X0]> <1, [7-(1)->6]> < [1X0]> <1, [12-(3)->11]> < [0X2]> <1, [O2] [11-(1)->10]> < [0X1]> <1, [O1] [36-(2)->35]> < [0X2]> <1, [O2] [7-(1)->4]> < [1X0]> <1, [31-(1)->29]> < [0X1]> <1, [O1] [41-(1)->39]> < [0X1]> <1, [O1] [33-(1)->30]> < [1X0]> <1, [27-(1)->26]> < [0X1]> <1, [27-(1)->26]> < [0X1]> <1, [O1] [17-(1)->19]> < [1X0]> <1, [2-(1)->8]> < [1X0]> <1, [end phase] [21-(2)->20]> <0, [-(3)->16] [-(1)->8] [13-(2)->14]> < [1X0]> <0, [16-(1)->15]> < [0X1]> <0, [O6] [24-(1)->10]> < [0X1]> <0, [O1] [34-(1)->39]> < [0X1]> <0, [O2] [28-(2)->26]> < [0X1]> <0, [O2] [15-(2)->17]> < [0X1]> <0, [O5] [17-(2)->22]> < [1X0]> <0, [8-(2)->3]> < [1X0]> <0, [17-(3)->32]> < [0X1]> <0, [O2] [0-(2)->5]> < [0X1]> <0, [O1] [13-(1)->14]> < [1X0]> <0, [32-(1)->21]> < [1X0]> <0, [8-(1)->2]> < [1X0]> <0, [17-(1)->18]> < [1X0]> <0, [40-(1)->41]> < [0X1]> <0, [O1] [34-(1)->27]> < [0X1]> <0, [O1] [30-(1)->31]> < [1X0]> <0, [6-(1)->7]> < [1X0]> <0, [16-(1)->18]> < [0X1]> <0, [O1] [0-(1)->31]> < [1X0]> <0, [26-(1)->36]> < [1X0]> <0, [13-(1)->14]> < [0X1]> <0, [O1] [end phase] [19-(1)->15]> <1, [-(2)->36] [-(3)->2] [35-(1)->27]> < [1X0]> <1, [36-(2)->26]> < [1X0]> <1, [36-(1)->18]> < [1X0]> <1, [end phase] [20-(1)->21]> <0, [-(6)->5] [-(1)->39] [-(6)->10] [-(2)->8] [8-(2)->3]> < [0X1]> <0, [O2] [10-(3)->11]> < [1X0]> <0, [10-(2)->9]> < [2X0]> <0, [10-(1)->9]> < [1X0]> <0, [10-(2)->12]> < [2X0]> <0, [5-(2)->7]> < [1X0]> <0, [5-(3)->7]> < [0X1]> <0, [O2] [end phase] [5-(2)->4]> <1, [-(1)->33] [-(3)->9] [-(1)->2] [2-(3)->8]> < [1X0]> <1, [2-(1)->3]> < [0X1]> <1, [2-(2)->3]> < [1X0]> <1, [20-(2)->24]> < [0X1]> <1, [O2] [12-(1)->10]> < [0X1]> <1, [O2] [2-(1)->3]> < [0X1]> <1, [O2] [3-(1)->7]> < [1X0]> <1, [33-(1)->27]> < [0X1]> <1, [O1] [21-(2)->32]> < [0X1]> <1, [O2] [32-(1)->28]> < [1X0]> <1, [end phase] [23-(1)->25]> <0, [-(4)->17] [-(8)->13] [-(2)->4] [19-(2)->24]> < [1X1]> <0, [17-(3)->32]> < [0X1]> <0, [O4] [32-(1)->22]> < [1X0]> <0, [32-(1)->22]> < [1X0]> <0, [19-(1)->24]> < [1X0]> <0, [32-(1)->21]> < [0X1]> <0, [O1] [7-(1)->3]> < [1X0]> <0, [end phase] [13-(5)->16]> <1, [-(6)->24] [-(2)->37] [24-(2)->19]> < [0X1]> <1, [O6] [19-(1)->15]> < [1X0]> <1, [25-(2)->21]> < [1X0]> <1, [20-(1)->21]> < [0X1]> <1, [O1] [19-(3)->17]> < [0X1]> <1, [O2] [19-(2)->15]> < [2X0]> <1, [17-(1)->15]> < [1X0]> <1, [end phase] [9-(1)->11]> <0, [-(6)->26] [-(3)->32] [32-(3)->21]> < [0X1]> <0, [O3] [21-(1)->22]> < [1X0]> <0, [15-(1)->17]> < [0X1]> <0, [O1] [26-(3)->36]> < [0X1]> <0, [O2] [13-(2)->19]> < [1X0]> <0, [26-(2)->27]> < [0X1]> <0, [O2] [end phase] [16-(2)->18]> <1, [-(3)->29] [-(4)->20] [end phase] [20-(4)->24]> <0, [-(9)->8] [-(1)->38] [8-(3)->2]> < [1X0]> <0, [8-(3)->3]> < [0X1]> <0, [O5] [36-(1)->35]> < [1X0]> <0, [8-(3)->2]> < [0X1]> <0, [O1] [21-(1)->23]> < [1X0]> <0, [27-(1)->35]> < [0X1]> <0, [O1] [13-(1)->19]> < [0X1]> <0, [O1] [end phase] [4-(4)->14]> <1, [-(5)->25] [-(1)->29] [24-(1)->17]> < [1X0]> <1, [25-(3)->21]> < [0X1]> <1, [O6] [21-(2)->32]> < [1X0]> <1, [37-(2)->35]> < [1X0]> <1, [24-(3)->17]> < [1X0]> <1, [29-(3)->35]> < [0X1]> <1, [O3] [21-(3)->32]> < [0X1]> <1, [O2] [35-(1)->36]> < [1X0]> <1, [32-(1)->28]> < [0X1]> <1, [O1] [24-(1)->19]> < [0X1]> <1, [O1] [end phase] [23-(1)->21]> <0, [C[1, 2, 4]] [-(10)->15] [-(9)->26] [26-(2)->28]> < [1X0]> <0, [26-(2)->32]> < [1X0]> <0, [26-(1)->28]> < [0X1]> <0, [O6] [15-(1)->19]> < [0X1]> <0, [O8] [28-(1)->32]> < [0X1]> <0, [O1] [19-(1)->24]> < [1X0]> <0, [end phase] [19-(4)->17]>";

    List<String[]> rawPlies = Arrays.stream(pliesString.split("> <"))
        .map(s -> s.substring(s.indexOf('['))).map(s -> s.split("] \\["))
        .collect(Collectors.toList());

    List<String> plies = new ArrayList<>();
    for (String[] rawPly : rawPlies) {
      for (String ply : rawPly) {
        plies.add(ply);
      }
    }

    plies = plies.stream().map(s -> {
      int start = s.indexOf('[') + 1;
      int end = s.indexOf(']');
      if (end > start) {
        return s.substring(start, end);
      }
      return s.substring(start);
    }).collect(Collectors.toList());

    List<RiskAction> actions = plies.stream().map(RiskAction::fromString)
        .collect(Collectors.toList());

    Risk risk = new Risk();
    for (RiskAction action : actions) {
      risk = (Risk) risk.doAction(action);
    }

    System.out.println();

  }

  /*
  @Property(trials = 64)
  public void prop_gameOverEquivPossibleActionsEmpty(@From(RiskGenerator.class) Risk risk) {
    assertEquals(
        risk.toTextRepresentation() + "\n#############################\n" + risk.getActionRecords()
            .toString(),
        risk.isGameOver(),
        risk.getPossibleActions().isEmpty());
  }


  @Property(trials = 64)
  public void prop_onlyPossibleActionsAreValid(@From(RiskGenerator.class) Risk risk,
      @From(RiskActionGenerator.class) RiskAction action) {

    fail();
    assertTrue(!risk.isValidAction(action) || risk.getPossibleActions().contains(action));

  }

  @Property(trials = 64)
  public void prop_doingValidActionDoesNotThrowException(@From(RiskGenerator.class) Risk risk) {
    for (RiskAction possibleAction : risk.getPossibleActions()) {
      try {
        risk.doAction(possibleAction);
      } catch (Exception e) {
        fail();
      }
    }
  }

  @Property(trials = 64)
  public void prop_anyValidCanonicalActionIsAlsoValidNonCanonicalAction(
      @From(RiskGenerator.class) //TODO: Only generate canonical games
                                     Risk risk,
      @From(RiskActionGenerator.class) RiskAction action) {
    assertEquals(risk.isValidAction(action), risk.getGame().isValidAction(action));
  }

  @Property(trials = 64)
  public void prop_allPossibleCanonicalActionsAreAlsoPossibleNonCanonicalActions(
      @From(RiskGenerator.class) Risk risk) {

    assertTrue(risk.getGame().getPossibleActions().containsAll(risk.getPossibleActions()));

  }

  @Property(trials = 64)
  public void prop_allPossibleActionsAreValid(@From(RiskGenerator.class) Risk risk) {
    Set<RiskAction> possibleActions = risk.getPossibleActions();
    for (RiskAction action : possibleActions) {
      assertTrue(risk.isValidAction(action));
    }
  }
  */

  @Test
  public void test_fullGame() {
    RiskConfiguration riskConfiguration = RiskConfiguration.getYaml().load(simpleConfigYaml);
    riskConfiguration.setChooseInitialTerritories(true);
    Risk risk = new Risk(riskConfiguration, 2);
    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(2));
    risk = (Risk) risk.doAction(RiskAction.select(0));
    risk = (Risk) risk.doAction(RiskAction.select(1));
    risk = (Risk) risk.doAction(RiskAction.select(1));

    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 2, 3));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 1));
    risk = (Risk) risk.doAction(RiskAction.occupy(3));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(0, 2, 3));
    risk = (Risk) risk.doAction(RiskAction.casualties(1, 1));
    risk = (Risk) risk.doAction(RiskAction.attack(0, 2, 2));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));
    risk = (Risk) risk.doAction(RiskAction.occupy(2));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.reinforce(1, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(1, 2, 3));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 2));
    risk = (Risk) risk.doAction(RiskAction.occupy(1));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.reinforce(0, 3));
    risk = (Risk) risk.doAction(RiskAction.attack(0, 2, 3));
    risk = (Risk) risk.doAction(RiskAction.casualties(0, 1));
    risk = (Risk) risk.doAction(RiskAction.occupy(1));
    risk = (Risk) risk.doAction(RiskAction.endPhase());
    risk = (Risk) risk.doAction(RiskAction.endPhase());

    assertTrue(risk.getBoard().hasToTradeInCards(risk.getCurrentPlayer()));
    assertTrue(risk.getBoard().canTradeInAsSet(Set.of(0, 1), risk.getCurrentPlayer()));
    risk = (Risk) risk.doAction(RiskAction.playCards(0, 1));
    Set<RiskAction> possibleActions = risk.getPossibleActions();
    assertTrue(possibleActions.stream().allMatch(RiskAction::isBonus));
    assertEquals(1, possibleActions.size());

    RiskAction determinedAction = risk.determineNextAction();
    assertNotEquals(null, determinedAction);
    assertTrue(risk.isValidAction(determinedAction));


  }

  @Test
  public void test_tooSmallFields() {
    RiskConfiguration config = RiskConfiguration.getYaml().load(simpleConfigYaml);
    config.setInitialTroops(new int[]{10000});

    Risk risk = new Risk(config, 2);

    for (int i = 0; i < 1999; i++) {
      risk = (Risk) risk.doAction(risk.getPossibleActions().stream().findFirst().get());
    }

    System.out.println(risk.toTextRepresentation());

  }

}
