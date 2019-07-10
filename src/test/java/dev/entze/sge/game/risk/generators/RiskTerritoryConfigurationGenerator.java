package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;

public class RiskTerritoryConfigurationGenerator extends Generator<RiskTerritoryConfiguration> {

  public RiskTerritoryConfigurationGenerator() {
    super(RiskTerritoryConfiguration.class);
  }

  @Override
  public RiskTerritoryConfiguration generate(SourceOfRandomness random, GenerationStatus status) {

    return null;
  }
}
