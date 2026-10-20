package com.yarukov.is_lab_1.controller;

import com.yarukov.is_lab_1.service.CitySpecialOperationsService;
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

@Named("specialOpsBean") 
@ViewScoped
@Getter
@Setter
public class SpecialOperationsBean implements Serializable {

    @Inject
    private CitySpecialOperationsService specialOpsService;

    @Inject
    @Push(channel = "cityChannel")
    private PushContext pushContext;
    
    private Double targetMeters;
    private Long countAboveMetersResult;
    private Double sumMetersResult;
    private List<Double> uniqueMetersList;
    private Long relocateFromCityId;
    
    public void calculateSumMeters() {
        this.sumMetersResult = specialOpsService.calculateSumMetersAboveSeaLevel();
        addMessage(FacesMessage.SEVERITY_INFO, "Результат", "Сумма высот: " + sumMetersResult);
    }
    
    public void calculateCountAboveMeters() {
        if (targetMeters == null) {
            addMessage(FacesMessage.SEVERITY_WARN, "Внимание", "Укажите значение высоты");
            return;
        }
        this.countAboveMetersResult = specialOpsService.countMetersAboveSeaLevelGreaterThan(targetMeters);
        addMessage(FacesMessage.SEVERITY_INFO, "Результат", "Найдено городов: " + countAboveMetersResult);
    }

    public void calculateUniqueMeters() {
        this.uniqueMetersList = specialOpsService.getUniqueMetersAboveSeaLevel();
        addMessage(FacesMessage.SEVERITY_INFO, "Результат", "Уникальные значения получены");
    }


    public void relocateToMinCity() {
        try {
            if (relocateFromCityId == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Внимание", "Укажите ID города");
                return;
            }
            specialOpsService.relocatePopulationToMinCity(relocateFromCityId);
            notifyAllClients();
            addMessage(FacesMessage.SEVERITY_INFO, "Успех", "Жители успешно переселены");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка переселения", e.getMessage());
        }
    }

    public void relocateCapital() {
        try {
            specialOpsService.relocateCapitalPopulationToThreeMinCities();
            notifyAllClients();
            addMessage(FacesMessage.SEVERITY_INFO, "Успех", "50% жителей столицы успешно распределены");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Ошибка переселения", e.getMessage());
        }
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