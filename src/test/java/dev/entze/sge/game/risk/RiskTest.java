package dev.entze.sge.game.risk;

import dev.entze.sge.game.risk.RiskConfiguration.RiskConfiguration;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class RiskTest {


  @Test
  public void test_yaml_1() {

    String yamlString = ""
        + "maxNumberOfPlayers: 2\n"
        + "initialTroops: [2]\n"
        + "numberOfJokers: 2\n"
        + "cardTypes: 1\n"
        + "continents:\n"
        + "  - continentId: 0\n"
        + "    troopBonus: 1\n"
        + "  - continentId: 1\n"
        + "    troopBonus: 1\n"

        + "territories:\n"
        + "  - territoryId: 0\n"
        + "    cardType: 0\n"
        + "    continentId: 0\n"
        + "    connects: [1]\n"
        + "  - territoryId: 1\n"
        + "    cardType: 1\n"
        + "    continentId: 1\n"
        + "    connects: [0]\n"

        + "map: \"[%s] [%s]\"";

    Yaml yaml = RiskConfiguration.getYaml();

    RiskConfiguration loaded = yaml.load(yamlString);

    loaded.toString();

  }

  @Test
  public void test_yaml_dump_0() {
    Yaml yaml = RiskConfiguration.getYaml();

    RiskConfiguration riskConfiguration = new RiskConfiguration();
    riskConfiguration.setMaxNumberOfPlayers(2);
    riskConfiguration.setInitialTroops(2);
    riskConfiguration.setWithCards(true);
    riskConfiguration.setCardTypes(1);
    riskConfiguration.setNumberOfJokers(1);
    riskConfiguration.setMap("[%s]<-->[%s]");

    System.out.println(yaml.dump(riskConfiguration));

  }


}