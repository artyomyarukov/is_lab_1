package com.yarukov.is_lab_1.controller;

import com.yarukov.is_lab_1.model.*;
import com.yarukov.is_lab_1.service.CityService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named("cityBean")
@ViewScoped
@Getter
@Setter
public class CityBean implements Serializable {

    @Inject
    private CityService cityService;

    @Inject
    @Push(channel = "cityChannel")
    private PushContext pushContext;

    private Long selectedCoordinatesId;
    private List<City> cities;
    private City selectedCity;
    private Float newCoordX;
    private Integer newCoordY;
    private Human newGovernor;

    @PostConstruct
    public void init() {
        refreshData();
        resetForms();
    }

    public void refreshData() {
        this.cities = cityService.getAllCities();
    }

    public void resetForms() {
        this.selectedCoordinatesId = null;
        this.selectedCity = new City();
        this.newCoordX = null;
        this.newCoordY = null;
        this.newGovernor = new Human();
    }

    public void openNew() {
        resetForms();
    }

    public void saveCity() {
        try {
            Long governorAge = (newGovernor != null) ? newGovernor.getAge() : null;
            cityService.saveCityFromForm(selectedCity, selectedCoordinatesId, newCoordX, newCoordY, governorAge);
            refreshData();
            notifyAllClients();
            addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Город успешно сохранен");
            resetForms();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", e.getMessage());
        }
    }

    public void editCity(City city) {
        this.selectedCity = city;
        this.selectedCoordinatesId = (city.getCoordinates() != null)
                ? city.getCoordinates().getId() : null;

        if (city.getCoordinates() != null) {
            this.newCoordX = city.getCoordinates().getX();
            this.newCoordY = city.getCoordinates().getY();
        } else {
            this.newCoordX = null;
            this.newCoordY = null;
        }

        if (city.getGovernor() != null) {
            this.newGovernor = Human.builder()
                    .id(city.getGovernor().getId())
                    .age(city.getGovernor().getAge())
                    .build();
        } else {
            this.newGovernor = new Human();
        }
    }

    public void deleteCity(Long id) {
        try {
            cityService.deleteCity(id);
            refreshData();
            notifyAllClients();
            addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Город удален!");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка удаления", e.getMessage());
        }
    }


    public Climate[] getClimates() {
        return Climate.values();
    }

    public Government[] getGovernments() {
        return Government.values();
    }

    public List<Coordinates> getAllCoordinates() {
        return cityService.getAllCoordinates();
    }

    private void notifyAllClients() {
        if (pushContext != null) {
            pushContext.send("updateTable");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}