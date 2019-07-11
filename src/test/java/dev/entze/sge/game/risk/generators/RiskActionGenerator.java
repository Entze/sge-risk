package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.board.RiskAction;

public class RiskActionGenerator extends Generator<RiskAction> {

  public RiskActionGenerator() {
    super(RiskAction.class);
  }

  @Override
  public RiskAction generate(SourceOfRandomness random, GenerationStatus status) {
    int roll = random.nextInt(8);
    RiskAction action = null;
    if (roll == 0) {
      action = RiskAction.attack(random.nextInt(), random.nextInt(), random.nextInt());
    } else if (roll == 1) {
      action = RiskAction.casualties(random.nextInt(), random.nextInt());
    } else if (roll == 2) {
      action = RiskAction.endPhase();
    } else if (roll == 3) {
      action = RiskAction.fortify(random.nextInt(), random.nextInt(), random.nextInt());
    } else if (roll == 4) {
      action = RiskAction.occupy(random.nextInt());
    } else if (roll == 5) {
      action = RiskAction.playCard(random.nextInt());
    } else if (roll == 6) {
      action = RiskAction.reinforce(random.nextInt(), random.nextInt());
    } else if (roll == 7) {
      action = RiskAction.select(random.nextInt());
    }
    return action;
  }
}
