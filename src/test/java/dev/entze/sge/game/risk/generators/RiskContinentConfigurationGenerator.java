package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;

public class RiskContinentConfigurationGenerator extends Generator<RiskContinentConfiguration> {

  public RiskContinentConfigurationGenerator() {
    super(RiskContinentConfiguration.class);
  }

  @Override
  public RiskContinentConfiguration generate(SourceOfRandomness random, GenerationStatus status) {
    int id = Math.abs(random.nextInt());
    int troopBonus = Math.abs(random.nextInt());
    return new RiskContinentConfiguration(id, troopBonus);
  }
}
