package at.ac.tuwien.ifs.sge.game.risk.generators;

import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskTerritoryConfiguration;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import at.ac.tuwien.ifs.sge.game.risk.Util;
import java.util.HashSet;
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
    Set<Integer> connects = new HashSet<>(Util.ints(random, 128));

    connects.remove(id);

    while (connects.isEmpty()) {
      connects.add(Math.abs(random.nextInt()));
      connects.remove(id);
    }

    return new RiskTerritoryConfiguration(id, cardType, continentId, connects);
  }
}
