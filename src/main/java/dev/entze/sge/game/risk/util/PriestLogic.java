package dev.entze.sge.game.risk.util;

public enum PriestLogic {

  FALSE(-1),
  UNKNOWN(0),
  TRUE(1);


  private int internalValue;

  PriestLogic(int internalValue) {
    this.internalValue = internalValue;
  }

  public static PriestLogic fromBoolean(boolean bool) {
    if (bool) {
      return TRUE;
    }
    return FALSE;
  }

  private static PriestLogic invertFromInternalValue(int internalValue) {
    if (internalValue < 0) {
      return FALSE;
    } else if (internalValue == 0) {
      return UNKNOWN;
    }
    return TRUE;
  }

  private static PriestLogic not(int internalValue) {
    return invertFromInternalValue(-1 * internalValue);
  }

  public static PriestLogic not(PriestLogic a) {
    return not(a.internalValue);
  }

  private static PriestLogic and(PriestLogic a, PriestLogic b) {
    return and(a.internalValue, b.internalValue);
  }

  private static PriestLogic and(int a, int b) {
    return invertFromInternalValue(Math.min(a, b));
  }

  public static PriestLogic or(PriestLogic a, PriestLogic b) {
    return or(a.internalValue, b.internalValue);
  }

  private static PriestLogic or(int a, int b) {
    return invertFromInternalValue(Math.max(a, b));
  }

  public static PriestLogic implies(PriestLogic a, PriestLogic b) {
    return implies(a.internalValue, b.internalValue);
  }

  private static PriestLogic implies(int a, int b) {
    return or(-1 * a, b);
  }

  private static PriestLogic xor(int a, int b) {
    return or(and(a, -1 * b), and(-1 * a, b));
  }

  private static PriestLogic equivalence(int a, int b) {
    return and(implies(a, b), implies(b, a));
  }

  public static PriestLogic equivalence(PriestLogic a, PriestLogic b) {
    return equivalence(a.internalValue, b.internalValue);
  }

  public static PriestLogic nand(PriestLogic a, PriestLogic b) {
    return nand(a.internalValue, b.internalValue);
  }

  private static PriestLogic nand(int a, int b) {
    return not(and(a, b));
  }

  public static PriestLogic nor(PriestLogic a, PriestLogic b) {
    return nor(a.internalValue, b.internalValue);
  }

  private static PriestLogic nor(int a, int b) {
    return not(or(a, b));
  }

  public static PriestLogic maybe(PriestLogic a, PriestLogic b) {
    if (a == UNKNOWN || b == UNKNOWN) {
      return UNKNOWN;
    }
    if (a == b) {
      return a;
    }
    return UNKNOWN;

  }


  public static boolean possible(PriestLogic a) {
    return a != FALSE;
  }

  public static boolean impossible(PriestLogic a) {
    return a == FALSE;
  }

  public static boolean certain(PriestLogic a) {
    return a != UNKNOWN;
  }

  public static boolean uncertain(PriestLogic a) {
    return a == UNKNOWN;
  }

  public static boolean falsifiable(PriestLogic a) {
    return a != TRUE;
  }

  public static boolean valid(PriestLogic a) {
    return a == TRUE;
  }

}
