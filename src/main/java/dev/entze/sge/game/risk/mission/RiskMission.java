package dev.entze.sge.game.risk.mission;

import java.util.List;

public class RiskMission {

  private RiskMissionType riskMissionType;
  private List<Integer> targetIds;

  public RiskMission() {
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

}
