package dev.entze.sge.game.risk;

import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import java.util.Set;
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


}