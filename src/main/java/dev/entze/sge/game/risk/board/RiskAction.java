package dev.entze.sge.game.risk.board;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskAction {

  private static final RiskAction END_PHASE = new RiskAction(-2, -4, -8);
  private static final int CASUALTIES_ID = -1;
  private static final int OCCUPY_ID = -2;
  private static final int CARD_ID = -3;
  private static final int BONUS_ID = -4;

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
    return new RiskAction(CARD_ID, CARD_ID, idsToSlotIds(ids));
  }

  public static RiskAction playCards(Iterable<Integer> ids) {
    return new RiskAction(CARD_ID, CARD_ID, RiskAction.idsToSlotIds(ids));
  }

  public static RiskAction bonusCards(int nr) {
    return new RiskAction(BONUS_ID, BONUS_ID, nr);
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
    return RiskAction.slotIdsToIds(value);
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

  public int getBonus() {
    return value;
  }

  public boolean isEndPhase() {
    return srcId == END_PHASE.srcId && targetId == END_PHASE.targetId && value == END_PHASE.value;
  }

  public boolean isCardIds() {
    return srcId == CARD_ID && targetId == CARD_ID;
  }

  public boolean isBonus() {
    return srcId == BONUS_ID && targetId == BONUS_ID;
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
      return "C".concat(playedCards().toString());
    }

    if (srcId == targetId && srcId == BONUS_ID) {
      return "B" + value;
    }

    if (srcId == -1) {
      return "-(" + value + ")->" + targetId;
    }

    return srcId + "-(" + value + ")->" + targetId;

  }

  public static RiskAction fromString(String string) {
    if (string.equals("end phase")) {
      return endPhase();
    }

    if (string.startsWith("O")) {
      int troops = Integer.parseInt(string.substring(1));
      return occupy(troops);
    }
    if (string.contains("X")) {
      String[] casualties = string.split("X");
      int attacker = Integer.parseInt(casualties[0]);
      int defender = Integer.parseInt(casualties[1]);
      return casualties(attacker, defender);
    }

    if (string.startsWith("B")) {
      return bonusCards(Integer.parseInt(string.substring(1)));
    }

    if (string.startsWith("C[")) {
      string = string.substring(2, string.length() - 1);
      return playCards(Arrays.stream(string.split(", ")).map(Integer::parseInt)
          .collect(Collectors.toUnmodifiableSet()));
    }

    if (string.startsWith("-(")) {
      int troopsStringEnd = string.indexOf(')');
      String troopsString = string.substring(2, troopsStringEnd);

      int troops = Integer.parseInt(troopsString);
      int destination = Integer.parseInt(string.substring(troopsStringEnd + 3));

      return reinforce(destination, troops);
    }

    if (string.contains("(")) {
      int srcStart = 0;
      int srcEnd = string.indexOf('(') - 1;

      int troopStart = srcEnd + 2;
      int troopEnd = string.indexOf(')');

      int destStart = troopEnd + 3;

      return attack(Integer.parseInt(string.substring(srcStart, srcEnd)),
          Integer.parseInt(string.substring(destStart)),
          Integer.parseInt(string.substring(troopStart, troopEnd)));

    }

    return null;
  }


  public static int idsToSlotIds(int... ids) {
    int value = 0;
    for (int id : ids) {
      value |= (1 << id);
    }
    return value;
  }


  public static int idsToSlotIds(Iterable<Integer> ids) {
    int value = 0;
    for (int id : ids) {
      value |= (1 << id);
    }
    return value;
  }

  public static Set<Integer> slotIdsToIds(final int ids) {
    return IntStream.range(0, Integer.SIZE - Integer.numberOfLeadingZeros(ids))
        .filter(i -> ((ids & (1 << i)) >>> i) != 0)
        .boxed().collect(Collectors.toSet());
  }


}
