package com.yarukov.is_lab_1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "lab1_humans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Human implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Возраст должен быть больше 0")
    @Max(value = 150, message = "Возраст губернатора не может превышать 150 лет")
    @Column(name = "age")
    private Long age;
}