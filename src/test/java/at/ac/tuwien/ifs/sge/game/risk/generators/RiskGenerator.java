package at.ac.tuwien.ifs.sge.game.risk.generators;

import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskConfiguration;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import at.ac.tuwien.ifs.sge.game.risk.Util;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import java.util.Set;

public class RiskGenerator extends Generator<Risk> {

  public RiskGenerator() {
    super(Risk.class);
  }

  @Override
  public Risk generate(SourceOfRandomness random, GenerationStatus status) {

    RiskConfiguration config = RiskConfiguration.RISK_DEFAULT_CONFIG;
    if (random.nextBoolean()) {
      config = new RiskConfigurationGenerator().generate(random, status);
    }

    int maxMult = 0;
    do {
      maxMult++;
    } while (random.nextBoolean());

    int playMoves = Util.gaussianInt(random.nextGaussian(), 0, 8 * maxMult);

    Risk risk = new Risk(config, random.nextInt(2, config.getMaxNumberOfPlayers()));

    if (random.nextInt(0, 128) == 64) {
      risk = (Risk) risk.getGame();
    }

    for (int i = 0; i < playMoves && !risk.isGameOver(); i++) {
      Set<RiskAction> possibleActions = risk.getPossibleActions();
      if (possibleActions.isEmpty()) {
        return risk;
      }
      risk = (Risk) risk.doAction(random.choose(possibleActions));
      if (random.nextInt(0, 128) == 64) {
        risk = (Risk) risk.getGame();
      }
    }

    return risk;

  }
}
