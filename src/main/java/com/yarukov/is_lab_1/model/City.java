package com.yarukov.is_lab_1.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cities")
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

    @NotNull(message = "Название города не может быть пустым (null)")
    @NotBlank(message = "Название города не может состоять из пробелов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Координаты обязательны для заполнения")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Площадь должна быть строго больше 0")
    @Column(name = "area", nullable = false)
    private double area;

    @NotNull(message = "Население не может быть пустым (null)")
    @Min(value = 1, message = "Население должно быть строго больше 0")
    @Column(name = "population", nullable = false)
    private Long population;

    @Column(name = "establishment_date")
    private ZonedDateTime establishmentDate;

    @Column(name = "capital")
    private Boolean capital;

    @Column(name = "meters_above_sea_level")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "governor_id")
    private Human governor;

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = LocalDateTime.now();
        }
    }
}