package com.yarukov.is_lab_1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Coordinates implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Координата X не может быть null")
    @DecimalMax(value = "176.0", message = "Максимальное значение X: 176")
    @Column(name = "x", nullable = false)
    private Float x;

    @NotNull(message = "Координата Y не может быть null")
    @Column(name = "y", nullable = false)
    private Integer y;
}