package com.yarukov.is_lab_1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "lab1_cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class City implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @NotNull(message = "Название города не может быть пустым")
    @NotBlank(message = "Название города не может состоять из пробелов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Координаты обязательны для заполнения")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Size(min = 1, max = 100, message = "Длина названия должна быть от 1 до 100 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s\\-]+$",
            message = "Название города может содержать только буквы (рус/англ), пробел и знак дефиса '-'")
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Площадь должна быть строго больше 0")
    @Column(name = "area", nullable = false)
    @DecimalMax(value = "1000000000.0", message = "Площадь не может превышать 1 млрд кв. км!")
    private double area;

    @NotNull(message = "Население не может быть пустым")
    @Min(value = 0, message = "Население не может быть отрицательным")
    @Max(value = 100000000000L, message = "Население не может превышать 100 млрд человек!")
    @Column(name = "population", nullable = false)
    private Long population;

    @Column(name = "establishment_date")
    private ZonedDateTime establishmentDate;

    @Column(name = "capital")
    private Boolean capital;

    @Column(name = "meters_above_sea_level")
    @DecimalMin(value = "-15000.0", message = "Высота не может быть ниже -15 000 м")
    @DecimalMax(value = "15000.0", message = "Высота не может превышать 15 000 м")
    private Double metersAboveSeaLevel;

    @Min(value = 1, message = "Код автомобиля должен быть больше 0")
    @Max(value = 1000, message = "Максимальный код автомобиля: 1000")
    @Column(name = "car_code")
    private Integer carCode;

    @NotNull(message = "Климат обязателен для выбора")
    @Enumerated(EnumType.STRING)
    @Column(name = "climate", nullable = false)
    private Climate climate;

    @Enumerated(EnumType.STRING)
    @Column(name = "government")
    private Government government;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "governor_id")
    private Human governor;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = LocalDateTime.now();
        }
    }
}