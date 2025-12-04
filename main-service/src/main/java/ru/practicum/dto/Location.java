package ru.practicum.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {

    @NotNull
    @DecimalMin(value = "-90.0", message = "Широта не может быть меньше -90")
    @DecimalMax(value = "90.0", message = "Широта не может быть больше 90")
    Float lat;

    @NotNull
    @DecimalMin(value = "-180.0", message = "Долгота не может быть меньше -180")
    @DecimalMax(value = "180.0", message = "Долгота не может быть больше 180")
    Float lon;
}
