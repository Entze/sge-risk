package at.ac.tuwien.ifs.sge.game.risk.generators;

import at.ac.tuwien.ifs.sge.game.risk.mission.RiskMissionType;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import at.ac.tuwien.ifs.sge.game.risk.Util;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskMissionConfiguration;
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
