package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentRespondDto;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemOwnerDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private List<CommentRespondDto> comments;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;
}
