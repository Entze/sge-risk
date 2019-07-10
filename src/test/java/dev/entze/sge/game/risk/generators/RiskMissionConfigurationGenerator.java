package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.Util;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayList;

public class RiskMissionConfigurationGenerator extends Generator<RiskMissionConfiguration> {

  public RiskMissionConfigurationGenerator() {
    super(RiskMissionConfiguration.class);
  }

  @Override
  public RiskMissionConfiguration generate(SourceOfRandomness random, GenerationStatus status) {
    RiskMissionType type = RiskMissionType.WILDCARD;
    while (type == RiskMissionType.WILDCARD) {
      type = random.choose(RiskMissionType.values());
    }
    return new RiskMissionConfiguration(type,
        new ArrayList<>(Util.ints(random, 128)), Math.abs(random.nextInt()));
  }
}
