package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.ValidationMarker;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestInputDto {
    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String description;
}
