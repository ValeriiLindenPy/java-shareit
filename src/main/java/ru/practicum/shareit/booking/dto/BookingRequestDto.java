package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.error.ValidationMarker;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Long itemId;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private LocalDateTime start;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private LocalDateTime end;
}
