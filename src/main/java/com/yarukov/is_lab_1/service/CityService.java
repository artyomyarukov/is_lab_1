package com.yarukov.is_lab_1.service;

import com.yarukov.is_lab_1.model.City;
import com.yarukov.is_lab_1.model.Coordinates;
import com.yarukov.is_lab_1.model.Human;
import com.yarukov.is_lab_1.repository.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class CityService implements Serializable {

    @Inject
    private CityRepository cityRepository;

    public void saveCity(City city) {
        if (city.getId() == null) {
            cityRepository.create(city);
        } else {
            cityRepository.update(city);
        }
    }


    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Город с id " + id + " не найден в системе"));
    }


    public void deleteCity(Long id) {
        City cityToDelete = findById(id);
        List<City> allCities = this.getAllCities();

        boolean coordinatesInUse = allCities.stream()
                .anyMatch(c -> !c.getId().equals(id) &&
                        c.getCoordinates() != null &&
                        c.getCoordinates().getId().equals(cityToDelete.getCoordinates().getId()));

        if (coordinatesInUse) {
            throw new IllegalStateException("Невозможно удалить город: его координаты используются другим городом");
        }

        if (cityToDelete.getGovernor() != null) {
            boolean governorInUse = allCities.stream()
                    .anyMatch(c -> !c.getId().equals(id) &&
                            c.getGovernor() != null &&
                            c.getGovernor().getId().equals(cityToDelete.getGovernor().getId()));

            if (governorInUse) {
                throw new IllegalStateException("Невозможно удалить город: его губернатор управляет еще одним городом");
            }
        }

        cityRepository.delete(id);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<Coordinates> getAllCoordinates() {
        return cityRepository.findAllCoordinates();
    }

    public void saveCoordinates(Coordinates coordinates) {
        cityRepository.createCoordinates(coordinates);
    }

    public List<Human> getAllHumans() {
        return cityRepository.findAllHumans();
    }

    public void saveHuman(Human human) {
        cityRepository.createHuman(human);
    }


    public void saveCityFromForm(City city, Long coordinatesId, Float newX, Integer newY, Long governorAge) {
        if (coordinatesId != null) {
            Coordinates existingCoords = getAllCoordinates().stream()
                    .filter(c -> c.getId().equals(coordinatesId))
                    .findFirst()
                    .orElse(null);
            city.setCoordinates(existingCoords);

        } else if (newX != null && newY != null) {
            if (city.getId() != null && city.getCoordinates() != null) {
                city.getCoordinates().setX(newX);
                city.getCoordinates().setY(newY);
            } else {
                Coordinates created = Coordinates.builder().x(newX).y(newY).build();
                saveCoordinates(created);
                city.setCoordinates(created);
            }
        } else if (city.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты обязательны: выберите из списка или введите новые");
        }

        if (governorAge != null) {
            if (city.getId() != null && city.getGovernor() != null) {
                city.getGovernor().setAge(governorAge);
            } else {
                Human newHuman = Human.builder().age(governorAge).build();
                saveHuman(newHuman);
                city.setGovernor(newHuman);
            }
        }
        saveCity(city);
    }



}