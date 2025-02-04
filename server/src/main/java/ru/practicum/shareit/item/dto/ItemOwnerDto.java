package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemOwnerDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private List<CommentResponseDto> comments;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastBooking;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextBooking;
}
