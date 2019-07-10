package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import java.util.Set;

public class RiskTerritoryConfigurationGenerator extends Generator<RiskTerritoryConfiguration> {

  public RiskTerritoryConfigurationGenerator() {
    super(RiskTerritoryConfiguration.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public RiskTerritoryConfiguration generate(SourceOfRandomness random, GenerationStatus status) {
    int id = Math.abs(random.nextInt());
    int cardType = Math.abs(random.nextInt());
    int continentId = Math.abs(random.nextInt());
    Set<Integer> connects = gen().type(Set.class, Integer.class).generate(random, status);

    connects.remove(id);

    return new RiskTerritoryConfiguration(id, cardType, continentId, connects);
  }
}
