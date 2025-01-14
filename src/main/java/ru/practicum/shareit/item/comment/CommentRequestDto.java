package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.error.ValidationMarker;

@Data
@Builder
public class CommentRequestDto {
    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String text;
}
