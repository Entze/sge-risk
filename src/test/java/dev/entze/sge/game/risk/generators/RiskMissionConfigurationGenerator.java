package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayList;
import java.util.Collection;

public class RiskMissionConfigurationGenerator extends Generator<RiskMissionConfiguration> {

  public RiskMissionConfigurationGenerator() {
    super(RiskMissionConfiguration.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public RiskMissionConfiguration generate(SourceOfRandomness random, GenerationStatus status) {
    int roll = random.nextInt(4);
    RiskMissionConfiguration mission = null;
    Generator<Collection> ints = gen().type(Collection.class, Integer.class);
    if (roll == 0) {
      mission = random.choose(RiskMissionConfiguration
          .occupyTerritories(Math.abs(random.nextInt()), Math.abs(random.nextInt())));
    } else if (roll == 1) {
      int to = random.nextInt();
      int from = random.nextInt(to);
      mission = random.choose(RiskMissionConfiguration.liberatePlayer(from, to));
    } else if (roll == 2) {
      RiskMissionType type = RiskMissionType.WILDCARD;
      while (type == RiskMissionType.WILDCARD) {
        type = random.choose(RiskMissionType.values());
      }
      mission = new RiskMissionConfiguration(type,
          new ArrayList<Integer>(ints.generate(random, status)));
    } else if (roll == 3) {
      RiskMissionType type = RiskMissionType.WILDCARD;
      while (type == RiskMissionType.WILDCARD) {
        type = random.choose(RiskMissionType.values());
      }
      mission = new RiskMissionConfiguration(type,
          new ArrayList<Integer>(ints.generate(random, status)), Math.abs(random.nextInt()));
    }

    return mission;
  }
}
