package dev.entze.sge.game.risk.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import dev.entze.sge.game.risk.Util;
import dev.entze.sge.game.risk.configuration.RiskConfiguration;
import dev.entze.sge.game.risk.configuration.RiskContinentConfiguration;
import dev.entze.sge.game.risk.configuration.RiskMissionConfiguration;
import dev.entze.sge.game.risk.configuration.RiskTerritoryConfiguration;
import dev.entze.sge.game.risk.mission.RiskMissionType;
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

    riskConfiguration.setMaxNumberOfPlayers(Math.abs(random.nextInt()));
    riskConfiguration.setMaxAttackerDice(Math.abs(random.nextInt()));
    riskConfiguration.setMaxDefenderDice(Math.abs(random.nextInt()));
    riskConfiguration.setWithCards(random.nextBoolean());
    riskConfiguration.setCardTypesWithoutJoker(Math.abs(random.nextInt()));
    riskConfiguration.setNumberOfJokers(Math.abs(random.nextInt()));
    riskConfiguration.setChooseInitialTerritories(random.nextBoolean());
    riskConfiguration.setReinforcementAtLeast(Math.abs(random.nextInt()));
    riskConfiguration.setReinforcementThreshold(Math.abs(random.nextInt()));
    riskConfiguration.setOccupyOnlyWithAttackingArmies(random.nextBoolean());
    riskConfiguration.setFortifyOnlyFromSingleTerritory(random.nextBoolean());
    riskConfiguration.setFortifyOnlyWithNonFightingArmies(random.nextBoolean());
    riskConfiguration.setWithMissions(random.nextBoolean());

    riskConfiguration.setContinents(
        gen().type(Set.class, RiskContinentConfiguration.class).generate(random, status));

    if (riskConfiguration.isWithMissions()) {
      riskConfiguration.setMissions(
          gen().type(Set.class, RiskMissionConfiguration.class).generate(random, status));
    }

    riskConfiguration.setTerritories(
        gen().type(Set.class, RiskTerritoryConfiguration.class).generate(random, status));

    Set<RiskContinentConfiguration> continents = riskConfiguration.getContinents();
    Set<RiskTerritoryConfiguration> territories = riskConfiguration.getTerritories();
    Set<RiskTerritoryConfiguration> validTerritories = new HashSet<>();
    Set<RiskMissionConfiguration> missions = riskConfiguration.getMissions();
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

      Set<Integer> connects = territory.getConnects();
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

    RiskMissionConfigurationGenerator riskMissionConfigurationGenerator = new RiskMissionConfigurationGenerator();
    while (riskConfiguration.isWithMissions()
        && riskConfiguration.getMaxNumberOfPlayers() < missions.size()) {
      missions.add(riskMissionConfigurationGenerator.generate(random, status));
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

    riskConfiguration.setTerritories(validTerritories);
    riskConfiguration.setMissions(validMissions);
    riskConfiguration.getInitialTroops();

    return riskConfiguration;
  }

}
