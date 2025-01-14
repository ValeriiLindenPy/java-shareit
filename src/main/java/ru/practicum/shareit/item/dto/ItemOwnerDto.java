package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.error.ValidationMarker;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemOwnerDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;
}
