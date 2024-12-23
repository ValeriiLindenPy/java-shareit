package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.error.ValidationMarker;


@Data
@Builder
public class ItemDto {
    @Null(groups = ValidationMarker.OnCreate.class)
    private Long id;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String name;

    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String description;

    @NotNull(groups = ValidationMarker.OnCreate.class, message = "Не указан available статус")
    private Boolean available;
}
