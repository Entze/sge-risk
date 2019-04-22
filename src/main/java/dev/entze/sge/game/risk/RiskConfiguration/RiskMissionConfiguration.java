package dev.entze.sge.game.risk.RiskConfiguration;

import dev.entze.sge.game.risk.mission.RiskMission;
import dev.entze.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskMissionConfiguration {

  private RiskMissionType missionType;
  private List<Integer> targetIds;
  private int occupyingWith = 0;

  public RiskMissionConfiguration() {
  }

  public RiskMissionConfiguration(RiskMissionType missionType,
      List<Integer> targetIds) {
    this.missionType = missionType;
    this.targetIds = targetIds;
  }

  public RiskMissionConfiguration(RiskMissionType missionType,
      List<Integer> targetIds, int occupyingWith) {
    this.missionType = missionType;
    this.targetIds = targetIds;
    this.occupyingWith = occupyingWith;
  }

  public static List<RiskMissionConfiguration> defaultMissions(int from, int to,
      Collection<Set<RiskContinentConfiguration>> continentGroups, int[] numberOfTerritories,
      int[] numberOfTroops) {
    List<RiskMissionConfiguration> defaultMissions = new ArrayList<>();
    defaultMissions.addAll(liberatePlayer(from, to));
    defaultMissions.addAll(conquerContinents(continentGroups));
    for (int i = 0; i < numberOfTerritories.length && i < numberOfTroops.length; i++) {
      defaultMissions.addAll(occupyTerritories(numberOfTerritories[i], numberOfTroops[i]));
    }
    return defaultMissions;
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

  public static List<RiskMissionConfiguration> conquerContinents(
      Collection<Set<RiskContinentConfiguration>> continentGroups) {
    return continentGroups.stream().map(
        continentGroup -> continentGroup.stream().map(RiskContinentConfiguration::getContinentId)
            .collect(Collectors.toList()))
        .map(ids -> new RiskMissionConfiguration(RiskMissionType.CONQUER_CONTINENT, ids))
        .collect(Collectors.toList());
  }

  public static Set<RiskContinentConfiguration> continentGroup(
      RiskContinentConfiguration... continents) {
    return new HashSet<>(Arrays.asList(continents));
  }


  public static List<RiskMissionConfiguration> conquerContinents(int[][] continentGroups) {
    return Arrays.stream(continentGroups)
        .map(continents -> Arrays.stream(continents).boxed().collect(Collectors.toList()))
        .map(ids -> new RiskMissionConfiguration(RiskMissionType.CONQUER_CONTINENT, ids))
        .collect(Collectors.toList());
  }

  public static List<RiskMissionConfiguration> occupyTerritories(int numberOfTerritories,
      int numberOfTroops) {
    Integer[] ids = new Integer[numberOfTerritories];
    Arrays.fill(ids, RiskMission.WILDCARD_ID);
    return Collections.singletonList(
        new RiskMissionConfiguration(RiskMissionType.OCCUPY_TERRITORY, Arrays.asList(ids),
            numberOfTroops));
  }

}
