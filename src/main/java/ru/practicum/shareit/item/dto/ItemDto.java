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
    @Null(groups = ValidationMarker.OnCreate.class, message = "Id should be null")
    private Long id;

    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Name can't be blank")
    private String name;

    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Description can't be blank")
    private String description;

    @NotNull(groups = ValidationMarker.OnCreate.class, message = "available shouldn't be null")
    private Boolean available;
}
