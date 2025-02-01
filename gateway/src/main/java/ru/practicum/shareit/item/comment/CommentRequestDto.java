package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.error.ValidationMarker;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotBlank(groups = ValidationMarker.OnCreate.class)
    private String text;
}
