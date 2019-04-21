package dev.entze.sge.game.risk.RiskConfiguration;

import dev.entze.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskMissionConfiguration {

  private RiskMissionType missionType;
  private List<Integer> targetIds;

  public RiskMissionConfiguration() {
  }

  public RiskMissionConfiguration(RiskMissionType missionType,
      List<Integer> targetIds) {
    this.missionType = missionType;
    this.targetIds = targetIds;
  }

  public RiskMissionType getMissionType() {
    return missionType;
  }

  public void setMissionType(RiskMissionType missionType) {
    this.missionType = missionType;
  }

  public List<Integer> getTargetIds() {
    return targetIds;
  }

  public void setTargetIds(List<Integer> targetIds) {
    this.targetIds = targetIds;
  }

  public static List<RiskMissionConfiguration> liberatePlayer(int from, int to) {
    return IntStream.range(from, to)
        .mapToObj(i -> new RiskMissionConfiguration(RiskMissionType.LIBERATE_PLAYER,
            Collections.singletonList(i))).collect(Collectors.toList());
  }

}
