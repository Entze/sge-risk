package dev.entze.sge.game.risk;

import java.util.Objects;

public class RiskAction {

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

  public static RiskAction bolster(int bolsteringId, int bolsteredId, int troops) {
    return new RiskAction(bolsteringId, bolsteredId, troops);
  }

  public static RiskAction casualties(int attacker, int defender) {
    return new RiskAction(attacker | (defender << 3));
  }

  public int selected() {
    return targetId;
  }

  public int reinforced() {
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

  public int bolsteringId() {
    return srcId;
  }

  public int bosteredId() {
    return targetId;
  }

  public int attackerCasualties() {
    return value & 0b111;
  }

  public int defenderCasualties() {
    return (value >>> 3) & 0b111;
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
    if (srcId == targetId && srcId == -1) {
      return String.format("%dX%d", this.attackerCasualties(), this.defenderCasualties());
    }

    if (srcId == -1) {
      return "-(" + value + ")->" + targetId;
    }

    return srcId + "-(" + value + ")->" + targetId;

  }
}
