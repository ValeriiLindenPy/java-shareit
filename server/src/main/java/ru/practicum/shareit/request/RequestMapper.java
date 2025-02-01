package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;

import java.util.List;

public class RequestMapper {
    public static RequestOutputDto mapToOutputDto(ItemRequest request, List<ItemDto> items) {
        return RequestOutputDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
