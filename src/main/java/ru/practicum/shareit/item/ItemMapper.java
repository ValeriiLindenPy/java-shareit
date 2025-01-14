package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.time.LocalDateTime;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable()).build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item, LocalDateTime lastBooking,LocalDateTime nextBooking) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public static Item toItem(ItemDto dto) {
        return Item.builder().id(dto.getId()).name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable()).build();
    }
}
