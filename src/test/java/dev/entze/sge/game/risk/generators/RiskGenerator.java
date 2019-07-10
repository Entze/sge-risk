package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.Risk;
import dev.entze.sge.game.risk.RiskAction;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import java.util.Arrays;
import java.util.Set;

public class RiskGenerator extends Generator<Risk> {

  public RiskGenerator() {
    super(Risk.class);
  }

  @Override
  public Risk generate(SourceOfRandomness random, GenerationStatus status) {

    Set<RiskConfiguration> configs = Set
        .of(new RiskConfigurationGenerator().generate(random, status),
            RiskConfiguration.RISK_DEFAULT_CONFIG);
    RiskConfiguration config = random.choose(configs);

    Risk risk = new Risk(config, random.nextInt(2, config.getMaxNumberOfPlayers()));
    Risk[] playthrough = new Risk[32];
    if (random.nextInt(0, 128) == 64) {
      risk = (Risk) risk.getGame();
    }

    int counter = 0;
    Arrays.fill(playthrough, risk);

    while (!risk.isGameOver() && ((counter = (counter + 1) % playthrough.length) != 0 || random
        .nextBoolean())) {
      Set<RiskAction> actions = risk.getPossibleActions();
      if (actions.isEmpty()) {
        break;
      }
      risk = (Risk) risk.doAction(random.choose(actions));
      if (random.nextInt(0, 128) == 64) {
        risk = (Risk) risk.getGame();
      }
      playthrough[counter] = risk;
    }

    return random.choose(playthrough);

  }
}
