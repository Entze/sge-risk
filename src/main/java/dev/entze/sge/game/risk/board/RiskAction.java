package dev.entze.sge.game.risk.board;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskAction {

  private static final RiskAction END_PHASE = new RiskAction(-2, -4, -8);
  private static final int CASUALTIES_ID = -1;
  private static final int OCCUPY_ID = -2;
  private static final int CARD_ID = -3;

  private final int srcId;
  private final int targetId;
  private final int value;

  private RiskAction(int srcId, int targetId, int value) {
    this.srcId = srcId;
    this.targetId = targetId;
    this.value = value;
  }

  private RiskAction(int targetId, int value) {
    this.srcId = -1;
    this.targetId = targetId;
    this.value = value;
  }

  private RiskAction(int value) {
    this.srcId = -1;
    this.targetId = -1;
    this.value = value;
  }

  public static RiskAction select(int id) {
    return new RiskAction(id, 1);
  }

  public static RiskAction reinforce(int id, int troops) {
    return new RiskAction(id, troops);
  }

  public static RiskAction attack(int attackingId, int defendingId, int troops) {
    return new RiskAction(attackingId, defendingId, troops);
  }

  public static RiskAction occupy(int troops) {
    return new RiskAction(OCCUPY_ID, OCCUPY_ID, troops);
  }

  public static RiskAction fortify(int fortifyingId, int fortifiedId, int troops) {
    return new RiskAction(fortifyingId, fortifiedId, troops);
  }

  public static RiskAction endPhase() {
    return END_PHASE;
  }

  public static RiskAction casualties(int attacker, int defender) {
    return new RiskAction(CASUALTIES_ID, CASUALTIES_ID,
        attacker | (defender << (Integer.SIZE / 2)));
  }

  public static RiskAction cardSlots(int id) {
    return new RiskAction(CARD_ID, CARD_ID, id);
  }

  public static RiskAction playCards(int... ids) {
    int value = 0;
    for (int id : ids) {
      value |= (1 << id);
    }
    return new RiskAction(CARD_ID, CARD_ID, value);
  }

  public static RiskAction playCards(Iterable<Integer> ids) {
    int value = 0;
    for (int id : ids) {
      value |= (1 << id);
    }
    return new RiskAction(CARD_ID, CARD_ID, value);
  }

  public int selected() {
    return targetId;
  }

  public int reinforcedId() {
    return targetId;
  }

  public int attackingId() {
    return srcId;
  }

  public int defendingId() {
    return targetId;
  }

  public int troops() {
    return value;
  }

  public Set<Integer> playedCards() {
    return IntStream.range(0, Integer.SIZE - Integer.numberOfLeadingZeros(value))
        .filter(i -> ((value & (1 << i)) >>> i) != 0)
        .boxed().collect(Collectors.toSet());
  }

  public int fortifyingId() {
    return srcId;
  }

  public int fortifiedId() {
    return targetId;
  }

  public int attackerCasualties() {
    return value & (~0 >>> (Integer.SIZE / 2));
  }

  public int defenderCasualties() {
    return (value >>> (Integer.SIZE / 2)) & (~0 >>> (Integer.SIZE / 2));
  }

  public boolean isEndPhase() {
    return srcId == END_PHASE.srcId && targetId == END_PHASE.targetId && value == END_PHASE.value;
  }

  public boolean isCardIds() {
    return srcId == CARD_ID && targetId == CARD_ID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RiskAction that = (RiskAction) o;
    return srcId == that.srcId &&
        targetId == that.targetId &&
        value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(srcId, targetId, value);
  }

  @Override
  public String toString() {
    if (isEndPhase()) {
      return "end phase";
    }

    if (srcId == targetId && srcId == CASUALTIES_ID) {
      return String.format("%dX%d", this.attackerCasualties(), this.defenderCasualties());
    }

    if (srcId == targetId && srcId == OCCUPY_ID) {
      return "O" + value;
    }

    if (srcId == targetId && srcId == CARD_ID) {
      return "C" + playedCards().toString();
    }

    if (srcId == -1) {
      return "-(" + value + ")->" + targetId;
    }

    return srcId + "-(" + value + ")->" + targetId;

  }
}
