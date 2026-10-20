package com.yarukov.is_lab_1.service;

import com.yarukov.is_lab_1.model.City;
import com.yarukov.is_lab_1.repository.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CitySpecialOperationsService implements Serializable {

    @Inject
    private CityRepository cityRepository;

    @Inject
    private CityService cityService;

    public double calculateSumMetersAboveSeaLevel() {
        return cityRepository.findAll().stream()
                .map(City::getMetersAboveSeaLevel)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }


    public long countMetersAboveSeaLevelGreaterThan(double targetMeters) {
        return cityRepository.findAll().stream()
                .map(City::getMetersAboveSeaLevel)
                .filter(Objects::nonNull)
                .filter(meters -> meters > targetMeters)
                .count();
    }


    public List<Double> getUniqueMetersAboveSeaLevel() {
        return cityRepository.findAll().stream()
                .map(City::getMetersAboveSeaLevel)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void relocatePopulationToMinCity(Long fromCityId) {
        City fromCity = cityService.findById(fromCityId);
        Long movingPeople = fromCity.getPopulation();

        if (movingPeople == null || movingPeople <= 0) {
            throw new IllegalStateException("В выбранном городе нет жителей для переселения");
        }

        City minCity = cityRepository.findAll().stream()
                .filter(c -> !c.getId().equals(fromCityId))
                .min(Comparator.comparing(City::getPopulation))
                .orElseThrow(() -> new IllegalStateException("В системе нет другого города для переселения"));

        minCity.setPopulation(minCity.getPopulation() + movingPeople);
        fromCity.setPopulation(0L);

        cityRepository.update(minCity);
        cityRepository.update(fromCity);
    }


    public void relocateCapitalPopulationToThreeMinCities() {
        City capital = cityRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getCapital()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Столица в системе не найдена"));

        long capitalPopulation = capital.getPopulation() != null ? capital.getPopulation() : 0L;
        if (capitalPopulation < 2) {
            throw new IllegalStateException("В столице слишком мало жителей для деления");
        }

        List<City> threeMinCities = cityRepository.findAll().stream()
                .filter(c -> !c.getId().equals(capital.getId()))
                .sorted(Comparator.comparing(City::getPopulation))
                .limit(3)
                .toList();

        if (threeMinCities.size() < 3) {
            throw new IllegalStateException("Для выполнения операции в системе должно быть минимум 3 других города");
        }

        long migratingPeople = capitalPopulation / 2;
        long peoplePerCity = migratingPeople / 3;
        long remainder = migratingPeople % 3;

        capital.setPopulation(capitalPopulation - migratingPeople);
        cityRepository.update(capital);

        for (int i = 0; i < threeMinCities.size(); i++) {
            City target = threeMinCities.get(i);
            long bonus = peoplePerCity + (i == 0 ? remainder : 0);
            target.setPopulation(target.getPopulation() + bonus);
            cityRepository.update(target);
        }
    }
}