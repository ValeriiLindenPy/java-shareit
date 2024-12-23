package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.error.ValidationMarker;

@Data
@Builder
public class UserDto {
    @Null(groups = ValidationMarker.OnCreate.class)
    private Long id;

    private String name;

    @Email(groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Не указан email")
    private String email;
}
