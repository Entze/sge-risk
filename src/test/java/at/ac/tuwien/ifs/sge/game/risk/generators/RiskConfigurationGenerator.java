package at.ac.tuwien.ifs.sge.game.risk.generators;

import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskConfiguration;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskContinentConfiguration;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskTerritoryConfiguration;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import at.ac.tuwien.ifs.sge.game.risk.Util;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskMissionConfiguration;
import at.ac.tuwien.ifs.sge.game.risk.mission.RiskMissionType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RiskConfigurationGenerator extends Generator<RiskConfiguration> {

  public RiskConfigurationGenerator() {
    super(RiskConfiguration.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public RiskConfiguration generate(SourceOfRandomness random, GenerationStatus status) {
    RiskConfiguration riskConfiguration = new RiskConfiguration();

    riskConfiguration.setMaxNumberOfPlayers(random.nextInt(2, 8));
    riskConfiguration.setMaxAttackerDice(random.nextInt(1, 8));
    riskConfiguration.setMaxDefenderDice(random.nextInt(1, 8));
    riskConfiguration.setWithCards(random.nextBoolean());
    riskConfiguration.setCardTypesWithoutJoker(random.nextInt(2, 8));
    riskConfiguration
        .setNumberOfJokers(random.nextInt(riskConfiguration.getCardTypesWithoutJoker()));
    riskConfiguration.setChooseInitialTerritories(random.nextBoolean());
    riskConfiguration.setReinforcementAtLeast(random.nextInt(2, 8));
    riskConfiguration.setReinforcementThreshold(random.nextInt(2, 8));
    riskConfiguration.setOccupyOnlyWithAttackingArmies(random.nextBoolean());
    riskConfiguration.setFortifyOnlyFromSingleTerritory(random.nextBoolean());
    riskConfiguration.setFortifyOnlyWithNonFightingArmies(random.nextBoolean());
    riskConfiguration.setWithMissions(random.nextBoolean());

    Set<RiskTerritoryConfiguration> territories = new HashSet<>();
    {
      int numberOfTerritories = Util.gaussianInt(random.nextGaussian(), 2, 64);
      RiskTerritoryConfigurationGenerator generator = new RiskTerritoryConfigurationGenerator();
      for (int i = 0; i < numberOfTerritories; i++) {
        territories.add(generator.generate(random, status));
      }
    }
    Set<RiskContinentConfiguration> continents = new HashSet<>();
    {
      int numberOfContinents = Util.gaussianInt(random.nextGaussian(), 1, territories.size());
      RiskContinentConfigurationGenerator generator = new RiskContinentConfigurationGenerator();
      for (int i = 0; i < numberOfContinents; i++) {
        continents.add(generator.generate(random, status));
      }
    }

    Set<RiskMissionConfiguration> missions = new HashSet<>();
    {
      int numberOfMissions = riskConfiguration.getMaxNumberOfPlayers();
      RiskMissionConfigurationGenerator generator = new RiskMissionConfigurationGenerator();
      for (int i = 0; i < numberOfMissions; i++) {
        missions.add(generator.generate(random, status));
      }
      while (missions.stream()
          .noneMatch(m -> m.getMissionType() == RiskMissionType.OCCUPY_TERRITORY)) {
        missions.add(generator.generate(random, status));
      }
    }

    riskConfiguration.setContinents(new ArrayList<>(continents));
    riskConfiguration.setTerritories(new ArrayList<>(territories));
    if (riskConfiguration.isWithMissions()) {
      riskConfiguration.setMissions(new ArrayList<>(missions));
    }

    Set<RiskTerritoryConfiguration> validTerritories = new HashSet<>();
    Set<RiskMissionConfiguration> validMissions = new HashSet<>();

    for (RiskTerritoryConfiguration territory : territories) {
      int territoryId = territory.getTerritoryId();

      int cardType = territory.getCardType();
      {
        if (cardType > riskConfiguration.getCardTypesWithoutJoker()) {
          cardType = random.nextInt(0, riskConfiguration.getCardTypesWithoutJoker());
        }
      }

      int continentId = territory.getContinentId();
      {
        final int finalContinentId = continentId;
        if (continents.stream().noneMatch(c -> c.getContinentId() == finalContinentId)) {
          continentId = random.choose(continents).getContinentId();
        }
      }

      Set<Integer> connects = new HashSet<>(territory.getConnects());
      do {
        connects = connects.stream()
            .filter(i -> territories.stream().anyMatch(t -> t.getTerritoryId() == i))
            .collect(Collectors.toSet());
        int connectsWith = Util.gaussianInt(random.nextGaussian(), 2, territories.size());
        connectsWith -= connects.size();
        for (int i = 0; i < connectsWith; i++) {
          connects.add(random.choose(territories).getTerritoryId());
        }

        connects.remove(territoryId);
      } while (connects.isEmpty());

      validTerritories
          .add(new RiskTerritoryConfiguration(territoryId, cardType, continentId, connects));
    }

    for (RiskMissionConfiguration mission : missions) {
      RiskMissionType missionType = mission.getMissionType();
      List<Integer> targetIds = mission.getTargetIds();
      int occupyingWith = mission.getOccupyingWith();

      while (missionType == RiskMissionType.WILDCARD) {
        missionType = random.choose(RiskMissionType.values());
      }

      if (missionType == RiskMissionType.CONQUER_CONTINENT) {
        targetIds = targetIds.stream().distinct()
            .filter(i -> i < 0 || continents.stream().anyMatch(c -> c.getContinentId() == i))
            .collect(Collectors.toList());
        int conquerContinents = Util
            .gaussianInt(random.nextGaussian(), continents.size() / 2, continents.size() / 3);
        conquerContinents -= targetIds.size();

        for (int i = 0; i < conquerContinents; i++) {
          int continentId = random.choose(continents).getContinentId();
          if (targetIds.contains(continentId)) {
            targetIds.add(-1);
          } else {
            targetIds.add(continentId);
          }
        }

      } else if (missionType == RiskMissionType.OCCUPY_TERRITORY) {
        targetIds = targetIds.stream().distinct()
            .filter(i -> i < 0 || validTerritories.stream().anyMatch(t -> t.getTerritoryId() == i))
            .collect(Collectors.toList());

        int occupyTerritories = Util.gaussianInt(random.nextGaussian(), validTerritories.size() / 2,
            validTerritories.size() / 3);
        occupyTerritories -= targetIds.size();

        for (int i = 0; i < occupyTerritories; i++) {
          if (random.nextBoolean()) {
            targetIds.add(-1);
          } else {
            int id = random.choose(validTerritories).getTerritoryId();
            if (targetIds.contains(id)) {
              targetIds.add(-1);
            } else {
              targetIds.add(id);
            }
          }
        }

      } else if (missionType == RiskMissionType.LIBERATE_PLAYER) {
        targetIds = targetIds.stream().distinct()
            .filter(i -> i < riskConfiguration.getMaxNumberOfPlayers())
            .collect(Collectors.toList());

        int liberatePlayer = Util
            .gaussianInt(random.nextGaussian(), 1,
                Math.max(1, riskConfiguration.getMaxNumberOfPlayers() / 6));

        liberatePlayer -= targetIds.size();

        for (int i = 0; i < liberatePlayer; i++) {
          int id;
          do {
            id = random.nextInt(riskConfiguration.getMaxNumberOfPlayers());
          } while (targetIds.contains(id));
          targetIds.add(id);
        }

      }

      targetIds = targetIds.stream().map(t -> t < 0 ? -1 : t).collect(Collectors.toList());

      validMissions.add(new RiskMissionConfiguration(missionType, targetIds, occupyingWith));

    }

    riskConfiguration.setTerritories(new ArrayList<>(validTerritories));
    if (riskConfiguration.isWithMissions()) {
      riskConfiguration.setMissions(new ArrayList<>(validMissions));
    }
    riskConfiguration.getInitialTroops();

    StringBuilder stringBuilder = new StringBuilder();
    for (RiskTerritoryConfiguration validTerritory : validTerritories) {
      stringBuilder.append('[').append(validTerritory.getTerritoryId()).append(']').append(' ');
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    stringBuilder.append('\n');
    for (RiskTerritoryConfiguration validTerritory : validTerritories) {
      stringBuilder.append('{');
      for (Integer connect : validTerritory.getConnects()) {
        stringBuilder.append(connect).append(',');
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1).append('}').append(' ');
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

    riskConfiguration.setMap(stringBuilder.toString());
    return riskConfiguration;
  }

}
