package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
