package dev.entze.sge.game.risk.mission;

import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import java.util.List;

public class RiskMission {

  public static final int WILDCARD_ID = (-1);
  public static final RiskMission FALLBACK = RiskMissionConfiguration.occupyTerritories(24, 2)
      .get(0).getMission();

  private final RiskMissionType riskMissionType;
  private final List<Integer> targetIds;
  private final int occupyingWith;

  public RiskMission(RiskMissionType riskMissionType, List<Integer> targetIds, int occupyingWith) {
    this.riskMissionType = riskMissionType;
    this.targetIds = targetIds;
    this.occupyingWith = occupyingWith;
  }

  public RiskMission(RiskMissionType riskMissionType, List<Integer> targetIds) {
    this(riskMissionType, targetIds, 0);
  }

  public RiskMissionType getRiskMissionType() {
    return riskMissionType;
  }

  public List<Integer> getTargetIds() {
    return targetIds;
  }

  public int getOccupyingWith() {
    return occupyingWith;
  }

}
