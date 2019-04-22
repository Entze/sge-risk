package dev.entze.sge.game.risk.mission;

import java.util.List;

public class RiskMission {

  public static final int WILDCARD_ID = (-1);

  private RiskMissionType riskMissionType;
  private List<Integer> targetIds;
  private int occupyingWith = 0;

  public RiskMission() {
  }

  public RiskMission(RiskMissionType riskMissionType, List<Integer> targetIds, int occupyingWith) {
    this.riskMissionType = riskMissionType;
    this.targetIds = targetIds;
    this.occupyingWith = occupyingWith;
  }

  public RiskMission(RiskMissionType riskMissionType, List<Integer> targetIds) {
    this.riskMissionType = riskMissionType;
    this.targetIds = targetIds;
  }

  public RiskMissionType getRiskMissionType() {
    return riskMissionType;
  }

  public void setRiskMissionType(RiskMissionType riskMissionType) {
    this.riskMissionType = riskMissionType;
  }

  public List<Integer> getTargetIds() {
    return targetIds;
  }

  public void setTargetIds(List<Integer> targetIds) {
    this.targetIds = targetIds;
  }

  public int getOccupyingWith() {
    return occupyingWith;
  }

  public void setOccupyingWith(int occupyingWith) {
    this.occupyingWith = occupyingWith;
  }
}
