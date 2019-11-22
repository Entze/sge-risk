package at.ac.tuwien.ifs.sge.game.risk.generators;

import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class RiskActionGenerator extends Generator<RiskAction> {

  public RiskActionGenerator() {
    super(RiskAction.class);
  }

  @Override
  public RiskAction generate(SourceOfRandomness random, GenerationStatus status) {
    int roll = random.nextInt(9);
    RiskAction action = null;
    if (roll == 0) {
      action = RiskAction
          .attack(random.nextInt(Integer.MAX_VALUE), random.nextInt(Integer.MAX_VALUE),
              random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 1) {
      action = RiskAction
          .casualties(random.nextInt(Integer.MAX_VALUE), random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 2) {
      action = RiskAction.endPhase();
    } else if (roll == 3) {
      action = RiskAction
          .fortify(random.nextInt(Integer.MAX_VALUE), random.nextInt(Integer.MAX_VALUE),
              random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 4) {
      action = RiskAction.occupy(random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 5) {
      action = RiskAction.cardSlots(random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 6) {
      action = RiskAction
          .reinforce(random.nextInt(Integer.MAX_VALUE), random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 7) {
      action = RiskAction.select(random.nextInt(Integer.MAX_VALUE));
    } else if (roll == 8) {
      action = RiskAction.bonusTroopsFromCards(random.nextInt(Integer.MAX_VALUE));
    }
    return action;
  }
}
